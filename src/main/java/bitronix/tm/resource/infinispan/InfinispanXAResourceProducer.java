package bitronix.tm.resource.infinispan;

import bitronix.tm.internal.BitronixRuntimeException;
import bitronix.tm.internal.XAResourceHolderState;
import bitronix.tm.recovery.RecoveryException;
import bitronix.tm.resource.ResourceObjectFactory;
import bitronix.tm.resource.ResourceRegistrar;
import bitronix.tm.resource.common.RecoveryXAResourceHolder;
import bitronix.tm.resource.common.ResourceBean;
import bitronix.tm.resource.common.XAResourceHolder;
import bitronix.tm.resource.common.XAResourceProducer;
import bitronix.tm.resource.common.XAStatefulHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.transaction.xa.XAResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InfinispanXAResourceProducer
        extends ResourceBean implements XAResourceProducer {

    private static final Logger LOG
            = LoggerFactory.getLogger(InfinispanXAResourceProducer.class);

    private static final Map<String, InfinispanXAResourceProducer> PRODUCERS
            = new HashMap<String, InfinispanXAResourceProducer>();

    private final List<InfinispanXAResourceHolder> xaResourceHolders
            = new ArrayList<InfinispanXAResourceHolder>();

    private RecoveryXAResourceHolder recoveryXAResourceHolder = null;


    InfinispanXAResourceProducer() {
        setApplyTransactionTimeout(true);
    }


    /**
     * Register an XAResource of a cache with BTM.
     * The first time a XAResource is registered a new
     * InfinispanXAResourceProducer is created to hold it.
     *
     * @param uniqueName the uniqueName of this XAResourceProducer,
     *                   usually the cache's name.
     * @param xaResource the XAResource to be registered.
     */
    public static void registerXAResource(final String uniqueName,
                                          final XAResource xaResource) {
        synchronized (PRODUCERS) {
            InfinispanXAResourceProducer xaResourceProducer =
                    PRODUCERS.get(uniqueName);

            if (xaResourceProducer == null) {
                xaResourceProducer = new InfinispanXAResourceProducer();
                xaResourceProducer.setUniqueName(uniqueName);
                // the initial xaResource must be added before init() is called
                xaResourceProducer.addXAResource(xaResource);
                xaResourceProducer.init();

                PRODUCERS.put(uniqueName, xaResourceProducer);
            } else {
                xaResourceProducer.addXAResource(xaResource);
            }
        }
    }

    /**
     * Unregister an XAResource of a cache from BTM.
     *
     * @param uniqueName the uniqueName of this XAResourceProducer, usually the cache's name
     * @param xaResource the XAResource to be registered
     */
    public static synchronized void unregisterXAResource(String uniqueName, XAResource xaResource) {
        synchronized (PRODUCERS) {
            InfinispanXAResourceProducer xaResourceProducer = PRODUCERS.get(uniqueName);

            if (xaResourceProducer != null) {
                boolean found = xaResourceProducer.removeXAResource(xaResource);
                if (!found) {
                    LOG.error("no XAResource " + xaResource + " found in XAResourceProducer with name " + uniqueName);
                }
                if (xaResourceProducer.xaResourceHolders.isEmpty()) {
                    xaResourceProducer.close();
                    PRODUCERS.remove(uniqueName);
                }
            } else {
                LOG.error("no XAResourceProducer registered with name " + uniqueName);
            }
        }
    }


    private void addXAResource(XAResource xaResource) {
        synchronized (xaResourceHolders) {
            InfinispanXAResourceHolder xaResourceHolder = new InfinispanXAResourceHolder(xaResource, this);
            xaResourceHolders.add(xaResourceHolder);
        }
    }

    private boolean removeXAResource(XAResource xaResource) {
        synchronized (xaResourceHolders) {
            for (int i = 0; i < xaResourceHolders.size(); i++) {
                InfinispanXAResourceHolder xaResourceHolder = (InfinispanXAResourceHolder) xaResourceHolders.get(i);
                if (xaResourceHolder.getXAResource() == xaResource) {
                    xaResourceHolders.remove(i);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public XAResourceHolderState startRecovery() throws RecoveryException {
        synchronized (xaResourceHolders) {
            if (recoveryXAResourceHolder != null) {
                throw new RecoveryException("recovery already in progress on " + this);
            }

            if (xaResourceHolders.isEmpty()) {
                throw new RecoveryException("no XAResource registered, recovery cannot be done on " + this);
            }

            recoveryXAResourceHolder = new RecoveryXAResourceHolder((XAResourceHolder) xaResourceHolders.get(0));
            return new XAResourceHolderState(recoveryXAResourceHolder, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endRecovery() throws RecoveryException {
        recoveryXAResourceHolder = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setFailed(boolean failed) {
        // cache cannot fail as it's not connection oriented
    }

    /**
     * {@inheritDoc}
     */
    public XAResourceHolder findXAResourceHolder(XAResource xaResource) {
        synchronized (xaResourceHolders) {
            for (int i = 0; i < xaResourceHolders.size(); i++) {
                InfinispanXAResourceHolder xaResourceHolder = xaResourceHolders.get(i);
                if (xaResource == xaResourceHolder.getXAResource()) {
                    return xaResourceHolder;
                }
            }

            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void init() {
        try {
            ResourceRegistrar.register(this);
        } catch (RecoveryException e) {
            throw new BitronixRuntimeException("error recovering " + this, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        synchronized (xaResourceHolders) {
            xaResourceHolders.clear();
            ResourceRegistrar.unregister(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public XAStatefulHolder createPooledConnection(Object xaFactory, ResourceBean bean) throws Exception {
        throw new UnsupportedOperationException("Infinispan is not connection-oriented");
    }

    /**
     * {@inheritDoc}
     */
    public Reference getReference() throws NamingException {
        return new Reference(InfinispanXAResourceProducer.class.getName(),
                new StringRefAddr("uniqueName", getUniqueName()),
                ResourceObjectFactory.class.getName(), null);
    }

}
