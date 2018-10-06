/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.dependency.graph.osgi;

public enum Namespace {
    ALL("ALL"),
    PACKAGE("osgi.wiring.package"),
    SERVICE("osgi.service"),
    IDENTITY("osgi.identity"),
    EE("osgi.ee"),
    NATIVE("osgi.native"),
    CONTENT("osgi.content"),
    IMPLEMENTATION("osgi.implementation"),
    CONTRACT("osgi.contract"),
    BUNDLE("osgi.wiring.bundle"),
    HOST("osgi.wiring.host");

    String namespace;

    Namespace(final String namespace) {
        this.namespace = namespace;
    }

    public String ns() {
        return namespace;
    }
}
