/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.amitinside.dependency.graph.osgi;

import java.io.File;

import aQute.bnd.util.dto.DTO;

public final class CliConfiguration extends DTO {

    public File obrIndex;
    public File bundles;
    public boolean isDebug;
    public boolean checkCycle;
    public boolean checkOnlyService;
    public boolean showEdgeLabel;

}
