/*
 * Copyright © 2016 AT&T , Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.att.demo.hellobgp.impl;


import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.BindingTransactionChain;
import org.opendaylight.controller.md.sal.binding.api.ClusteredDataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification.ModificationType;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;




import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;

import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.AsyncTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionChain;
import org.opendaylight.controller.md.sal.common.api.data.TransactionChainListener;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;

import org.opendaylight.protocol.bgp.parser.impl.message.update.CommunityUtil;

import org.opendaylight.protocol.bgp.rib.DefaultRibReference;
import org.opendaylight.protocol.bgp.rib.RibReference;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.AsNumber;
//import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.AsNumber;
// bgp imports
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Prefix;
//import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
//import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.inet.rev150305.ipv4.routes.Ipv4Routes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.inet.rev150305.ipv4.routes.ipv4.routes.Ipv4Route;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.inet.rev150305.ipv4.routes.ipv4.routes.Ipv4RouteBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.inet.rev150305.ipv4.routes.ipv4.routes.Ipv4RouteKey;


import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.AttributesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.AggregatorBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.AigpBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.ClusterIdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.CommunitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.ExtendedCommunitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.LocalPrefBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.MultiExitDiscBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.OriginBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.OriginatorIdBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.attributes.aigp.AigpTlvBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.ApplicationRib;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.ApplicationRibId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.ApplicationRibKey;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.BgpRib;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.RibId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.Route;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.bgp.rib.Rib;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.bgp.rib.RibKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.bgp.rib.rib.LocRib;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.rib.Tables;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.rib.rev130925.rib.TablesKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.BgpOrigin;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.ClusterIdentifier;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.Ipv4AddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.ShortAsNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.UnicastSubsequentAddressFamily;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.extended.community.extended.community.RouteTargetExtendedCommunityCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.extended.community.extended.community.route.target.extended.community._case.RouteTargetExtendedCommunityBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.c.next.hop.Ipv4NextHopCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.types.rev130919.next.hop.c.next.hop.ipv4.next.hop._case.Ipv4NextHopBuilder;

//import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.PathId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.Update;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.Attributes;



import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HellobgpService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HelloBgpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HelloBgpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HelloBgpOutputBuilder;




import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.network.concepts.rev131125.AccumulatedIgpMetric;

import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology; 
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology; 
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey; 


import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopologyBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;


import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import org.opendaylight.yangtools.yang.data.api.schema.tree.DataTreeCandidate;
import org.opendaylight.yangtools.yang.data.api.schema.tree.DataTreeCandidateNode;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Instantiated for LocRib and table, listens on a particular LocRib ,
 * performs transcoding to BA form (message) and does stuff .
 */
public class HelloBgpListener implements ClusteredDataTreeChangeListener<Ipv4Route>, TransactionChainListener, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HelloBgpListener.class);

 
    private ListenerRegistration<HelloBgpListener> registration; 
    private DataBroker db; 

    private static final RibReference LOC_RIB_REF = new DefaultRibReference(InstanceIdentifier.create(BgpRib.class).child(Rib.class, new RibKey(Preconditions.checkNotNull(new RibId("example-bgp-rib")))));

    private boolean closed = false; 
    private BindingTransactionChain chain = null;
    protected long listenerScheduledRestartTime = 0;
    protected int listenerScheduledRestartEnforceCounter = 0;


    private final long listenerResetLimitInMillsec = 5 * 60 * 1000 ;
    private final int listenerResetEnforceCounter = 3;




    public HelloBgpListener(DataBroker db) { 
        LOG.info("Registering HelloBgpListener"); 
        this.db = db; 
        registerListener(db); 
        initTransactionChain() ;
        LOG.info("Finished Registering HelloBgpListener"); 
    } 
 
    private void registerListener(final DataBroker db) { 

        final InstanceIdentifier<Tables> tablesId = this.LOC_RIB_REF.getInstanceIdentifier().child(LocRib.class).child(Tables.class, 
		new TablesKey(Ipv4AddressFamily.class, UnicastSubsequentAddressFamily.class));

        final DataTreeIdentifier<Ipv4Route> id = new DataTreeIdentifier<>(LogicalDatastoreType.OPERATIONAL, getRouteWildcard(tablesId));


        final DataTreeIdentifier<LocRib> treeId = 
                        new DataTreeIdentifier<LocRib>(LogicalDatastoreType.OPERATIONAL, getWildcardPath()); 

        try { 
            LOG.info("HelloBgpListener Registering on path: {}", id.toString()); 
            //registration = db.registerDataTreeChangeListener(treeId, HelloBgpListener.this); 
            registration = db.registerDataTreeChangeListener(id, HelloBgpListener.this); 
        } catch (final Exception e) { 
            LOG.warn("HelloBgpListener registration failed"); 
            //TODO: Should we throw an exception here? 
        } 
    } 
 
    @Override 
    public void close() throws Exception { 
        if(registration != null) { 
            LOG.info("HelloBgpListener registraion closing"); 
            registration.close(); 
        } 
        destroyTransactionChain();
    } 


    @Override
    public synchronized void onDataTreeChanged(final Collection<DataTreeModification<Ipv4Route>> changes) {
        if (this.closed) {
            LOG.trace("Transaction chain was already closed, skipping update.");
            return;
        }
        // check if the transaction chain needed to be restarted due to a previous error
        if (restartTransactionChainOnDemand()) {
            LOG.debug("The data change {} is disregarded due to restart of listener {}", changes, this);
            return;
        }
        final ReadWriteTransaction trans = this.chain.newReadWriteTransaction();
        LOG.info("Received data change {} event with transaction {}", changes, trans.getIdentifier());
        final AtomicBoolean transactionInError = new AtomicBoolean(false);
        for (final DataTreeModification<Ipv4Route> change : changes) {
            try {
                routeChanged(change, trans);
            } catch (final RuntimeException e) {
                LOG.warn("Data change {} (transaction {}) was not completely propagated to listener {}", change, trans.getIdentifier(), this, e);
                // trans.cancel() is not supported by PingPongTransactionChain, so we just skip the problematic change
                // trans.submit() must be called first to unlock the current transaction chain, to make the chain closable
                // so we cannot exit the #onDataTreeChanged() yet
                transactionInError.set(true);
                break;
            }
        } 

        Futures.addCallback(trans.submit(), new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {
                // as we are enforcing trans.submit(), in some cases the transaction execution actually could be successfully even when an
                // exception is captured, thus #onTransactionChainFailed() never get invoked. Though the transaction chain remains usable,
                // the data loss will not be able to be recovered. Thus we schedule a listener restart here
                if (transactionInError.get()) {
                    LOG.warn("Transaction {} committed successfully while exception captured. Rescheduling a restart of listener {}", trans
                        .getIdentifier(), HelloBgpListener.this);
                    scheduleListenerRestart();
                } else {
                    LOG.trace("Transaction {} committed successfully", trans.getIdentifier());
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                // we do nothing but print out the log. Transaction chain restart will be done in #onTransactionChainFailed()
                LOG.error("Failed to propagate change (transaction {}) by listener {}", trans.getIdentifier(), HelloBgpListener.this, t);
            }
        });
    }

    @VisibleForTesting
    protected void routeChanged(final DataTreeModification<Ipv4Route> change, final ReadWriteTransaction trans) {
        final DataObjectModification<Ipv4Route> root = change.getRootNode();
        switch (root.getModificationType()) {
        case DELETE:
            //removeObject(trans, change.getRootPath().getRootIdentifier(), root.getDataBefore());
	    LOG.info("removeObject: {}", root.getDataBefore());
            break;
        case SUBTREE_MODIFIED:
        case WRITE:
            if (root.getDataBefore() != null) {
	        LOG.info("removeObjectWrite: {}", root.getDataBefore());
                //removeObject(trans, change.getRootPath().getRootIdentifier(), root.getDataBefore());
            }
            LOG.info("createObject {}", root.getDataAfter());
            break;
        default:
            throw new IllegalArgumentException("Unhandled modification type " + root.getModificationType());
        }
    }
 
    //@Override 
    public void onDataTreeChanged2(Collection<DataTreeModification<LocRib>> changes) { 
        LOG.info("HelloBgp onDataTreeChanged: {}", changes); 
 
        /* TODO:
         * Currently only handling changes to Global. 
         * Rest will be added later. 
         */ 
        for (DataTreeModification<LocRib> change : changes) { 
            final InstanceIdentifier<LocRib> key = change.getRootPath().getRootIdentifier(); 
            final DataObjectModification<LocRib> mod = change.getRootNode(); 
	    LOG.info("HelloBgpDataChangeKey:" + key.toString());
	    LOG.info("HelloBgpDataChange:" + mod.getModificationType());

	    LOG.info("HelloBgpDataChangeBefore:" + mod.getDataBefore());
	    LOG.info("HelloBgpDataChangeAfter:" + mod.getDataAfter());
        }

        /*
        for (DataTreeModification<LocRib> change : changes) { 
            final InstanceIdentifier<LocRib> key = change.getRootPath().getRootIdentifier(); 
            final DataObjectModification<LocRib> mod = change.getRootLocRib(); 
                switch (mod.getModificationType()) { 
                case DELETE: 
                    LOG.trace("Data deleted: {}", mod.getDataBefore()); 
                    //disconnect(mod); 
                    break; 
                case SUBTREE_MODIFIED: 
                    LOG.trace("Data modified: {} to {}", mod.getDataBefore(),mod.getDataAfter()); 
                    updateConnections(mod); 
                    break; 
                case WRITE: 
                    if (mod.getDataBefore() == null) { 
                        LOG.trace("Data added: {}", mod.getDataAfter()); 
                        connect(mod.getDataAfter()); 
                    } else { 
                        LOG.trace("Data modified: {} to {}", mod.getDataBefore(),mod.getDataAfter()); 
                        updateConnections(mod); 
                    } 
                    break; 
                default: 
                    throw new IllegalArgumentException("Unhandled modification type " + mod.getModificationType()); 
                } 
        } 
        */ 

    } 


    private InstanceIdentifier<Ipv4Route> getRouteWildcard(final InstanceIdentifier<Tables> tablesId) {
        return tablesId.child((Class)Ipv4Routes.class).child(Ipv4Route.class);
    }

    private InstanceIdentifier<LocRib> getWildcardPath() { 
        InstanceIdentifier<LocRib> path = InstanceIdentifier 
                        .create(BgpRib.class) 
                        .child(Rib.class, new RibKey(new RibId("example-bgp-rib"))).child(LocRib.class); 
        return path; 
    } 





/**
     * There are a few reasons we want to schedule a listener restart in a delayed manner:
     * 1. we should avoid restarting the listener as when the topology is big, there might be huge overhead
     *    rebuilding the whole linkstate topology again and again
     * 2. the #onTransactionChainFailed() normally get invoked after a delay. During that time gap, more
     *    data changes might still be pushed to #onDataTreeChanged(). And because #onTransactionChainFailed()
     *    is not invoked yet, listener restart/transaction chain restart is not done. Thus the new changes
     *    will still cause error and another #onTransactionChainFailed() might be invoked later. The listener
     *    will be restarted again in that case, which is unexpected. Restarting of transaction chain only introduce
     *    little overhead and it's okay to be restarted within a small time window
     *
     * Note: when the listener is restarted, we can disregard all the incoming data changes before the restart is
     * done, as after the listener unregister/reregister, the first #onDataTreeChanged() call will contain the a
     * complete set of existing changes
     *
     * @return if the listener get restarted, return true; otherwise false
     */
    @VisibleForTesting
    protected synchronized boolean restartTransactionChainOnDemand() {
        if (this.listenerScheduledRestartTime > 0) {
            // when the #this.listenerScheduledRestartTime timer timed out we can reset the listener, otherwise we should only reset the transaction chain
            if (System.currentTimeMillis() > this.listenerScheduledRestartTime) {
                // reset the the restart timer
                this.listenerScheduledRestartTime = 0;
                this.listenerScheduledRestartEnforceCounter = 0;
                resetListener();
                return true;
            } else {
                resetTransactionChain();
            }
        }
        return false;
    }

    @VisibleForTesting
    protected synchronized void scheduleListenerRestart() {
        if (0 == this.listenerScheduledRestartTime) {
            this.listenerScheduledRestartTime = System.currentTimeMillis() + this.listenerResetLimitInMillsec;
        } else if (System.currentTimeMillis() > this.listenerScheduledRestartTime
            && ++this.listenerScheduledRestartEnforceCounter < this.listenerResetEnforceCounter) {
            // if the transaction failure happens again, we will delay the listener restart up to #LISTENER_RESET_LIMIT_IN_MILLSEC times
            this.listenerScheduledRestartTime += this.listenerResetLimitInMillsec;
        }
        LOG.debug("A listener restart was scheduled at {} (current system time is {})", this.listenerScheduledRestartTime, System.currentTimeMillis());
    }

    /**
     * Reset the data change listener to its initial status
     * By resetting the listener we will be able to recover all the data lost before
     */
    @VisibleForTesting
    protected synchronized void resetListener() {
        Preconditions.checkNotNull(this.registration, "Listener on topology %s hasn't been initialized.", this);
	// TODO: need to reimplement
        //LOG.debug("Resetting data change listener for topology builder {}", getInstanceIdentifier());
        // unregister current listener to prevent incoming data tree change first
        //unregisterDataChangeListener();
        // create new transaction chain to reset the chain status
        //resetTransactionChain();
        // reset the operational topology data so that we can have clean status
        //destroyOperationalTopology();
        //initOperationalTopology();
        // re-register the data change listener to reset the operational topology
        // we are expecting to receive all the pre-exist route change on the next onDataTreeChanged() call
        //registerDataChangeListener();
    }


     /**
     * Reset the transaction chain only so that the PingPong transaction chain will become usable again.
     * However, there will be data loss if we do not apply the previous failed transaction again
     */
    @VisibleForTesting
    protected synchronized void resetTransactionChain() {
	// TODO: need to reimplement
        //LOG.debug("Resetting transaction chain for topology builder {}", getInstanceIdentifier());
        //destroyTransactionChain();
        //initTransactionChain();
    }

    /**
     * Reset a transaction chain by closing the current chain and starting a new one
     */
    private synchronized void initTransactionChain() {
        LOG.debug("Initializing transaction chain for topology {}", this);
        Preconditions.checkState(this.chain == null, "Transaction chain has to be closed before being initialized");
        this.chain = db.createTransactionChain(this);
    }

    /**
     * Destroy the current transaction chain
     */
    private synchronized void destroyTransactionChain() {
        if (this.chain != null) {
            LOG.debug("Destroy transaction chain for topology {}", this);
            // we cannot close the transaction chain, as it will close the AbstractDOMForwardedTransactionFactory
            // and the transaction factory cannot be reopen even if we recreate the transaction chain
            // so we abandon the chain directly
            // FIXME we want to close the transaction chain gracefully once the PingPongTransactionChain get improved
            // and the above problem get resolved.
//            try {
//                this.chain.close();
//            } catch (Exception e) {
//                // the close() may not succeed when the transaction chain is locked
//                LOG.error("Unable to close transaction chain {} for topology builder {}", this.chain, getInstanceIdentifier());
//            }
            this.chain = null;
        }
    }



   @Override
    public final synchronized void onTransactionChainFailed(final TransactionChain<?, ?> chain, final AsyncTransaction<?, ?> transaction, final Throwable cause) {
        LOG.error("Topology builder for failed in transaction ");
        //LOG.error("Topology builder for {} failed in transaction {}.", getInstanceIdentifier(), transaction != null ? transaction.getIdentifier() : null, cause);
        scheduleListenerRestart();
        restartTransactionChainOnDemand();
    }

    @Override
    public final void onTransactionChainSuccessful(final TransactionChain<?, ?> chain) {
        LOG.info("Topology builder  shut down");
        //LOG.info("Topology builder for {} shut down", getInstanceIdentifier());
    }

}


