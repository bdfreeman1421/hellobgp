/*
 * Copyright © 2016 AT&T , Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.att.demo.hellobgp.impl;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.rev160806.HellobgpService;


public class HellobgpProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(HellobgpProvider.class);
    private RpcRegistration<HellobgpService> helloBgpService; 

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("HellobgpProvider Session Initiated");
       // instantiate the DataBroker
        DataBroker db = session.getSALService(DataBroker.class);

        helloBgpService = session.addRpcImplementation(HellobgpService.class, new HelloBgpImpl(db));

    }

    @Override
    public void close() throws Exception {
        LOG.info("HellobgpProvider Closed");
    }

}
