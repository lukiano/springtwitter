package com.lucho.util;

//CHECKSTYLE:OFF
import org.hibernate.search.Environment;
import org.hibernate.search.SearchException;
import org.hibernate.search.backend.IndexingMonitor;
import org.hibernate.search.backend.LuceneWork;
import org.hibernate.search.backend.spi.BackendQueueProcessor;
import org.hibernate.search.engine.spi.SearchFactoryImplementor;
import org.hibernate.search.indexes.impl.DirectoryBasedIndexManager;
import org.hibernate.search.indexes.spi.IndexManager;
import org.hibernate.search.spi.WorkerBuildContext;
import org.hibernate.search.util.impl.JNDIHelper;
import org.hibernate.search.util.logging.impl.Log;
import org.hibernate.search.util.logging.impl.LoggerFactory;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Jms
 */
public class JMSBackendQueueProcessor implements BackendQueueProcessor {

    private String jmsQueueName;
    private String jmsConnectionFactoryName;
    private static final String JNDI_PREFIX = Environment.WORKER_PREFIX
            + "jndi.";
    private Properties properties;
    private Queue jmsQueue;
    private ConnectionFactory factory;
    private String indexName;
    private SearchFactoryImplementor searchFactory;
    public static final String JMS_CONNECTION_FACTORY = Environment.WORKER_PREFIX
            + "jms.connection_factory";
    public static final String JMS_QUEUE = Environment.WORKER_PREFIX
            + "jms.queue";
    private IndexManager indexManager;

    private static final Log log = LoggerFactory.make();

    @Override
    public void initialize(Properties props, WorkerBuildContext context,
            DirectoryBasedIndexManager indexManager) {
        // TODO proper exception if jms queues and connections are not there
        this.properties = props;
        this.indexManager = indexManager;
        this.jmsConnectionFactoryName = props
                .getProperty(JMS_CONNECTION_FACTORY);
        this.jmsQueueName = props.getProperty(JMS_QUEUE);
        this.indexName = indexManager.getIndexName();
        this.searchFactory = context.getUninitializedSearchFactory();
        prepareJMSTools();
    }

    public ConnectionFactory getJMSFactory() {
        return factory;
    }

    public Queue getJmsQueue() {
        return jmsQueue;
    }

    public String getJmsQueueName() {
        return jmsQueueName;
    }

    public void prepareJMSTools() {
        if (jmsQueue != null && factory != null) {
            return;
        }
        try {
            InitialContext initialContext = JNDIHelper.getInitialContext(
                    properties, JNDI_PREFIX);
            factory = (ConnectionFactory) initialContext
                    .lookup(jmsConnectionFactoryName);
            jmsQueue = (Queue) initialContext.lookup(jmsQueueName);

        } catch (NamingException e) {
            throw new SearchException(
                    "Unable to lookup Search queue ("
                            + (jmsQueueName != null ? jmsQueueName : "null")
                            + ") and connection factory ("
                            + (jmsConnectionFactoryName != null ? jmsConnectionFactoryName
                                    : "null") + ")", e);
        }
    }

    public SearchFactoryImplementor getSearchFactory() {
        return searchFactory;
    }

    public void close() {
        // no need to release anything
    }

    @Override
    public void applyWork(List<LuceneWork> workList, IndexingMonitor monitor) {
        if (workList == null) {
            throw new IllegalArgumentException("workList should not be null");
        }
        // TODO review this integration with the old Runnable-style execution
        Runnable operation = new JMSBackendQueueTask(indexName, workList,
                indexManager, this);
        operation.run();
    }

    @Override
    public void applyStreamWork(LuceneWork singleOperation,
            IndexingMonitor monitor) {
        applyWork(Collections.singletonList(singleOperation), monitor);
    }

    @Override
    public Lock getExclusiveWriteLock() {
        log.warnSuspiciousBackendDirectoryCombination(indexName);
        return new ReentrantLock(); // keep the invoker happy, still it's
                                    // useless
    }

    @Override
    public void indexMappingChanged() {
        // no-op
    }

}
