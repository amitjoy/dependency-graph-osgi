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

import java.io.File;

import aQute.bnd.util.dto.DTO;

public final class CliConfiguration extends DTO {

    public File obrIndex;
    public File bundles;
    public Namespace namespace;
    public String customNamespace;
    public boolean isDebug;
    public boolean checkCycle;
    public boolean showEdgeLabel;

}
