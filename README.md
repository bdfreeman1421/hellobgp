# hellobgp

Example program to listen for changes in the OPERATIONAL LocRib and then take action.

Installation:

1. Install ODL 
2. Add BGP, RESTONF, NETCONF-CONNECTOR, APIDOCS 

feature:install odl-bgpcep-bgp; feature:install odl-restconf; feature:install odl-netconf-connector-all; feature:install odl-mdsal-apidocs

3. Create bgp-rib, peer-rip, app-rib

4. Add one app-rib route

5. Install HelloBGP
    a. copy karaf/target/assembly/com/att/demo to ~odl/system/com/att/demo
    b. kar:install file:/home/odl/com/att/demo/hellobgp/features/target/hellobgp-features-1.0.0-SNAPSHOT.kar

6. Use apidoc/explorer to send in rpc to hello-bgp

Example of Add Ipv4Route

2016-08-22 22:27:22,150 | INFO  | on-dispatcher-97 | HelloBgpListener                 | 295 - com.att.demo.hellobgp.impl - 1.0.0.SNAPSHOT | createObject Ipv4Route{getAttributes=Attributes{getAggregator=Aggregator{getAsNumber=AsNumber [_value=64495], getNetworkAddress=Ipv4Address [_value=200.20.160.41], augmentations={}}, getAigp=Aigp{getAigpTlv=AigpTlv{getMetric=AccumulatedIgpMetric [_value=120], augmentations={}}, augmentations={}}, getCNextHop=Ipv4NextHopCase{getIpv4NextHop=Ipv4NextHop{getGlobal=Ipv4Address [_value=199.20.160.41], augmentations={}}, augmentations={}}, getClusterId=ClusterId{getCluster=[Ipv4Address [_value=40.40.40.40]], augmentations={}}, getCommunities=[Communities{getAsNumber=AsNumber [_value=65535], getSemantics=65282, augmentations={}}], getExtendedCommunities=[ExtendedCommunities{getExtendedCommunity=RouteTargetExtendedCommunityCase{getRouteTargetExtendedCommunity=RouteTargetExtendedCommunity{getGlobalAdministrator=AsNumber [_value=65020], getLocalAdministrator=[B@56b246b, augmentations={}}, augmentations={}}, augmentations={}}], getLocalPref=LocalPref{getPref=100, augmentations={}}, getMultiExitDisc=MultiExitDisc{getMed=0, augmentations={}}, getOrigin=Origin{getValue=Egp, augmentations={}}, getOriginatorId=OriginatorId{getOriginator=Ipv4Address [_value=41.41.41.41], augmentations={}}, augmentations={}}, getPrefix=Ipv4Prefix [_value=200.20.160.41/32], augmentations={}}


Example of Remove Ipv4Route

2016-08-22 22:27:46,934 | INFO  | n-dispatcher-103 | HelloBgpListener                 | 295 - com.att.demo.hellobgp.impl - 1.0.0.SNAPSHOT | removeObject: Ipv4Route{getAttributes=Attributes{getAggregator=Aggregator{getAsNumber=AsNumber [_value=64495], getNetworkAddress=Ipv4Address [_value=200.20.160.41], augmentations={}}, getAigp=Aigp{getAigpTlv=AigpTlv{getMetric=AccumulatedIgpMetric [_value=120], augmentations={}}, augmentations={}}, getCNextHop=Ipv4NextHopCase{getIpv4NextHop=Ipv4NextHop{getGlobal=Ipv4Address [_value=199.20.160.41], augmentations={}}, augmentations={}}, getClusterId=ClusterId{getCluster=[Ipv4Address [_value=40.40.40.40]], augmentations={}}, getCommunities=[Communities{getAsNumber=AsNumber [_value=65535], getSemantics=65282, augmentations={}}], getExtendedCommunities=[ExtendedCommunities{getExtendedCommunity=RouteTargetExtendedCommunityCase{getRouteTargetExtendedCommunity=RouteTargetExtendedCommunity{getGlobalAdministrator=AsNumber [_value=65020], getLocalAdministrator=[B@56b246b, augmentations={}}, augmentations={}}, augmentations={}}], getLocalPref=LocalPref{getPref=100, augmentations={}}, getMultiExitDisc=MultiExitDisc{getMed=0, augmentations={}}, getOrigin=Origin{getValue=Egp, augmentations={}}, getOriginatorId=OriginatorId{getOriginator=Ipv4Address [_value=41.41.41.41], augmentations={}}, augmentations={}}, getPrefix=Ipv4Prefix [_value=200.20.160.41/32], augmentations={}}
