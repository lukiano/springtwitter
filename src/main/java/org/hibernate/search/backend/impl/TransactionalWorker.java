package org.hibernate.search.backend.impl;

import java.util.Properties;
import javax.transaction.Synchronization;

import org.hibernate.annotations.common.AssertionFailure;
import org.hibernate.search.backend.spi.Work;
import org.hibernate.search.backend.spi.WorkType;
import org.hibernate.search.backend.spi.Worker;
import org.hibernate.search.engine.spi.EntityIndexBinder;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;
import org.hibernate.search.util.logging.impl.Log;

import org.hibernate.search.SearchException;
import org.hibernate.search.backend.TransactionContext;
import org.hibernate.search.spi.InstanceInitializer;
import org.hibernate.search.spi.WorkerBuildContext;
import org.hibernate.search.util.impl.WeakIdentityHashMap;
import org.hibernate.search.util.logging.impl.LoggerFactory;

/**
 * Queue works per transaction.
 * If out of transaction, the work is executed right away
 * <p/>
 * When <code>hibernate.search.worker.type</code> is set to <code>async</code>
 * the work is done in a separate thread (threads are pooled)
 *
 * @author Emmanuel Bernard
 */
public class TransactionalWorker implements Worker {

    //note: there is one Worker instance per SearchFactory, reused concurrently for all sessions.

    private static final Log log = LoggerFactory.make();

    //this is being used from different threads, but doesn't need a
    //synchronized map since for a given transaction, we have not concurrent access
    protected final WeakIdentityHashMap<Object, Synchronization> synchronizationPerTransaction = new WeakIdentityHashMap<Object, Synchronization>();
    private QueueingProcessor queueingProcessor;
    private SearchFactoryImplementor factory;
    private InstanceInitializer instanceInitializer;

    private boolean transactionExpected;

    public void performWork(Work<?> work, TransactionContext transactionContext) {
        final Class<?> entityType = instanceInitializer.getClassFromWork( work );
        EntityIndexBinder indexBindingForEntity = factory.getIndexBindingForEntity( entityType );
        if ( indexBindingForEntity == null
                && factory.getDocumentBuilderContainedEntity( entityType ) == null ) {
            throw new SearchException( "Unable to perform work. Entity Class is not @Indexed nor hosts @ContainedIn: " + entityType );
        }
        work = interceptWork(indexBindingForEntity, work);
        if (work == null) {
            //nothing to do
            return;
        }
            WorkQueue queue = new WorkQueue( factory );
            queueingProcessor.add( work, queue );
            queueingProcessor.prepareWorks( queue );
            queueingProcessor.performWorks( queue );
    }

    private <T> Work<T> interceptWork(EntityIndexBinder indexBindingForEntity, Work<T> work) {
        if (indexBindingForEntity == null) {
            return work;
        }
        EntityIndexingInterceptor<? super T> interceptor = (EntityIndexingInterceptor<? super T> ) indexBindingForEntity.getEntityIndexingInterceptor();
        if (interceptor == null) {
            return work;
        }
        IndexingOverride operation;
        switch ( work.getType() ) {
            case ADD:
                operation = interceptor.onAdd( work.getEntity() );
                break;
            case UPDATE:
                operation = interceptor.onUpdate( work.getEntity() );
                break;
            case DELETE:
                operation = interceptor.onDelete( work.getEntity() );
                break;
            case COLLECTION:
                operation = interceptor.onCollectionUpdate( work.getEntity() );
                break;
            case PURGE:
            case PURGE_ALL:
            case INDEX:
                operation = IndexingOverride.APPLY_DEFAULT;
                break;
            default:
                throw new AssertionFailure( "Unknown work type: " + work.getType() );
        }
        Work<T> result = work;
        Class<T> entityClass = work.getEntityClass();
        switch ( operation ) {
            case APPLY_DEFAULT:
                break;
            case SKIP:
                result = null;
                log.forceSkipIndexOperationViaInterception( entityClass, work.getType() );
                break;
            case UPDATE:
                result = new Work<T>( work.getEntity(), work.getId(), WorkType.UPDATE );
                log.forceUpdateOnIndexOperationViaInterception( entityClass, work.getType() );
                break;
            case REMOVE:
                //This works because other Work constructors are never used from WorkType ADD, UPDATE, REMOVE, COLLECTION
                //TODO should we force isIdentifierRollback to false if the operation is not a delete?
                result = new Work<T>( work.getEntity(), work.getId(), WorkType.DELETE, work.isIdentifierWasRolledBack() );
                log.forceRemoveOnIndexOperationViaInterception( entityClass, work.getType() );
                break;
            default:
                throw new AssertionFailure( "Unknown action type: " + operation );
        }
        return result;
    }

    public void initialize(Properties props, WorkerBuildContext context, QueueingProcessor queueingProcessor) {
        this.queueingProcessor = queueingProcessor;
        this.factory = context.getUninitializedSearchFactory();
        this.transactionExpected = context.isTransactionManagerExpected();
        this.instanceInitializer = context.getInstanceInitializer();
    }

    public void close() {
    }

    public void flushWorks(TransactionContext transactionContext) {
        if ( transactionContext.isTransactionInProgress() ) {
            Object transaction = transactionContext.getTransactionIdentifier();
            PostTransactionWorkQueueSynchronization txSync = (PostTransactionWorkQueueSynchronization)
                    synchronizationPerTransaction.get( transaction );
            if ( txSync != null && !txSync.isConsumed() ) {
                txSync.flushWorks();
            }
        }
    }
}
