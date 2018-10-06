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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import aQute.lib.io.IO;

public final class Application {

    public static void main(final String... args) throws Exception {
        boolean showEdgeLabel = false;
        boolean cycle = false;
        boolean debug = false;
        Namespace namespace = Namespace.ALL;

        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addOption("o", true, "OBR Index File Location");
        options.addOption("b", true, "Bundle List File Location");
        options.addOption("e", false, "Show Edge Labels");
        options.addOption("debug", false, "Turn on Debug Mode");
        options.addOption("cycle", false, "Check for Cycle Existence");
        final String description = "Namespace Type to Plot (" + Lists.newArrayList(Namespace.values())
                + ") (Default ALL)";
        options.addOption("ns", true, description);

        String obrIndexFile = null;
        String bundleListFile = null;
        try {
            final CommandLine line = parser.parse(options, args);
            if (!line.hasOption("o")) {
                throw new ParseException("OBR Index File is missing");
            }
            if (!line.hasOption("b")) {
                throw new ParseException("Bundle List File is missing");
            }
            if (line.hasOption("e")) {
                showEdgeLabel = true;
            }
            if (line.hasOption("debug")) {
                debug = true;
            }
            if (line.hasOption("cycle")) {
                cycle = true;
            }
            if (line.hasOption("ns")) {
                final Optional<Namespace> optType = Enums.getIfPresent(Namespace.class, line.getOptionValue("ns"));
                namespace = optType.or(Namespace.ALL);
            }
            obrIndexFile = line.getOptionValue("o");
            bundleListFile = line.getOptionValue("b");
        } catch (final ParseException exp) {
            System.err.println(exp.getMessage());
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("help", options);
            System.exit(-1);
        }

        final File obrIndex = IO.getFile(obrIndexFile);
        if (!obrIndex.exists()) {
            System.out.println("No OBR Index Found");
            System.exit(-1);
        }

        final File bundlesFile = IO.getFile(bundleListFile);
        if (!bundlesFile.exists()) {
            System.out.println("No Bundle List Found");
            System.exit(-1);
        }

        final CliConfiguration config = new CliConfiguration();
        config.isDebug = debug;
        config.bundles = bundlesFile;
        config.obrIndex = obrIndex;
        config.checkCycle = cycle;
        config.namespace = namespace;
        config.showEdgeLabel = showEdgeLabel;

        if (debug) {
            System.out.println("Cli Configuration => " + config);
        }

        final GraphConfigurer configurer = new GraphConfigurer(config);
        configurer.init();
    }

}
