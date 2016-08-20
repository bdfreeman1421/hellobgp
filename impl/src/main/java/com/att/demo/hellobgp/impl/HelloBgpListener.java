/*
 * Copyright © 2016 AT&T , Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.att.demo.hellobgp.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Future;


import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification.ModificationType;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;



import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;

import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
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
public class HelloBgpListener implements DataTreeChangeListener<LocRib>, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HelloBgpListener.class);

 
    private ListenerRegistration<HelloBgpListener> registration; 
    private DataBroker db; 
 
    public HelloBgpListener(DataBroker db) { 
        LOG.info("Registering HelloBgpListener"); 
        this.db = db; 
        registerListener(db); 
        LOG.info("Finished Registering HelloBgpListener"); 
    } 
 
    private void registerListener(final DataBroker db) { 
        final DataTreeIdentifier<LocRib> treeId = 
                        new DataTreeIdentifier<LocRib>(LogicalDatastoreType.OPERATIONAL, getWildcardPath()); 
//                        new DataTreeIdentifier<LocRib>(LogicalDatastoreType.CONFIGURATION, getWildcardPath()); 
        try { 
            LOG.info("HelloBgpListener Registering on path: {}", treeId.toString()); 
            registration = db.registerDataTreeChangeListener(treeId, HelloBgpListener.this); 
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
    } 
 
    @Override 
    public void onDataTreeChanged(Collection<DataTreeModification<LocRib>> changes) { 
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




    private InstanceIdentifier<LocRib> getWildcardPath() { 
        InstanceIdentifier<LocRib> path = InstanceIdentifier 
                        .create(BgpRib.class) 
                        .child(Rib.class, new RibKey(new RibId("example-bgp-rib"))).child(LocRib.class); 
        return path; 
    } 

}

