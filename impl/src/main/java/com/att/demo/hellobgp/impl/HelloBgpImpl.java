/*
 * Copyright © 2016 AT&T , Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.att.demo.hellobgp.impl;

import java.math.BigInteger;
import java.util.Collections;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.protocol.bgp.parser.impl.message.update.CommunityUtil;
//import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.AsNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.AsNumber;
// bgp imports
//import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Address;
//import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Address;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;

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

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.PathId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.Update;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.message.rev130919.path.attributes.Attributes;



import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HellobgpService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HelloBgpInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HelloBgpOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HelloBgpOutputBuilder;



import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.network.concepts.rev131125.AccumulatedIgpMetric;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.KeyedInstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;




public class HelloBgpImpl implements HellobgpService {

       private DataBroker db;

        public HelloBgpImpl(DataBroker db) {
            this.db = db;
        }


    @Override
    public Future<RpcResult<HelloBgpOutput>> helloBgp(HelloBgpInput input) {
        HelloBgpOutputBuilder hellobgpBuilder = new HelloBgpOutputBuilder();
        hellobgpBuilder.setGreating("Hello " + input.getName());




        // do bgp
        final Ipv4RouteBuilder ipv4RouteBuilder = new Ipv4RouteBuilder();
        ipv4RouteBuilder.setPrefix(new Ipv4Prefix("200.20.160.41/32"));
        //ipv4RouteBuilder.setKey(new Ipv4RouteKey(ipv4RouteBuilder.getPrefix()));
        Long pathId= 0L;
        ipv4RouteBuilder.setKey(new Ipv4RouteKey(new PathId(pathId++),ipv4RouteBuilder.getPrefix()));
        final AttributesBuilder attributesBuilder = new AttributesBuilder();
        attributesBuilder.setCNextHop(new Ipv4NextHopCaseBuilder().setIpv4NextHop(
            new Ipv4NextHopBuilder().setGlobal(new Ipv4Address("199.20.160.41")).build()).build());
        attributesBuilder.setMultiExitDisc(new MultiExitDiscBuilder().setMed(0L).build());
        attributesBuilder.setLocalPref(new LocalPrefBuilder().setPref(100L).build());
        attributesBuilder.setOriginatorId(new OriginatorIdBuilder().setOriginator(new Ipv4Address("41.41.41.41")).build());
        attributesBuilder.setOrigin(new OriginBuilder().setValue(BgpOrigin.Egp).build());
        attributesBuilder.setClusterId(new ClusterIdBuilder().setCluster(Collections.singletonList(new ClusterIdentifier("40.40.40.40"))).build());
        attributesBuilder.setAggregator(new AggregatorBuilder().setAsNumber(new AsNumber(64495L)).setNetworkAddress(new Ipv4Address("200.20.160.41")).build());
        attributesBuilder.setAigp(new AigpBuilder().setAigpTlv(new AigpTlvBuilder().setMetric(new AccumulatedIgpMetric(BigInteger.valueOf(120L))).build()).build());
        attributesBuilder.setCommunities(Lists.newArrayList(new CommunitiesBuilder(CommunityUtil.NO_ADVERTISE).build()));
        attributesBuilder.setExtendedCommunities(Lists.newArrayList(
            //new ExtendedCommunitiesBuilder().setTransitive(true).setExtendedCommunity(
            new ExtendedCommunitiesBuilder().setExtendedCommunity(
               new RouteTargetExtendedCommunityCaseBuilder().setRouteTargetExtendedCommunity(
                    new RouteTargetExtendedCommunityBuilder()
                        .setGlobalAdministrator(new ShortAsNumber(65020L))
                        .setLocalAdministrator(Ints.toByteArray(1234)).build()).build()).build()));
        ipv4RouteBuilder.setAttributes(attributesBuilder.build());

        final InstanceIdentifier TABLES_IID = KeyedInstanceIdentifier
                .builder(ApplicationRib.class, new ApplicationRibKey(new ApplicationRibId("example-app-rib")))
                .child(Tables.class, new TablesKey(Ipv4AddressFamily.class, UnicastSubsequentAddressFamily.class))
                .build();

        final InstanceIdentifier<Ipv4Routes> routesIId = TABLES_IID.child(Ipv4Routes.class);

        final WriteTransaction wTx = db.newWriteOnlyTransaction();
        wTx.put(LogicalDatastoreType.CONFIGURATION, routesIId.child(Ipv4Route.class, ipv4RouteBuilder.getKey()), ipv4RouteBuilder.build());
        wTx.submit();

        return RpcResultBuilder.success(hellobgpBuilder.build()).buildFuture();
    }

}

