/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.dependency.graph.osgi.cli;

public final class CliOptions {

    private CliOptions() {
        throw new IllegalAccessError("Cannot instantiate");
    }

    public static final String HELP_1 = "?";
    public static final String HELP_2 = "help";
    public static final String OBR_FILE = "obr";
    public static final String BUNDLE_FILE = "bundles";
    public static final String SHOW_EDGE = "edge";
    public static final String IS_DEBUG = "debug";
    public static final String CYCLE = "cycle";
    public static final String NAMESPACE = "ns";
    public static final String NAMESPACE_CUSTOM = "ns_custom";
}
