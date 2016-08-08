/*
 * Copyright © 2015 AT&T , Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.impl.rev141210;

import com.att.demo.hellobgp.impl.HellobgpProvider;

public class HellobgpModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.impl.rev141210.AbstractHellobgpModule {
    public HellobgpModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public HellobgpModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hellobgp.impl.rev141210.HellobgpModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        HellobgpProvider provider = new HellobgpProvider();
        getBrokerDependency().registerProvider(provider);
        return provider;
    }

}
