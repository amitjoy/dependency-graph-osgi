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

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import aQute.lib.io.IO;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public final class Application {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);

    static {
        logger.setLevel(Level.INFO);
    }

    public static void main(final String... args) throws Exception {
        boolean showEdgeLabel = false;
        boolean cycle = false;
        boolean isDebug = false;
        Namespace namespace = Namespace.ALL;

        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addOption("?", false, "Show Help");
        options.addOption("help", false, "Show Help");
        options.addOption("o", true, "OBR Index File Location");
        options.addOption("b", true, "Bundle List File Location");
        options.addOption("e", false, "Show Edge Labels");
        options.addOption("debug", false, "Turn on Debug Mode");
        options.addOption("cycle", false, "Check for Cycle Existence");
        final String description = "Namespace Type to Plot " + Lists.newArrayList(Namespace.values())
                + " (Default ALL)";
        options.addOption("ns", true, description);

        String obrIndexFile = null;
        String bundleListFile = null;
        try {
            final CommandLine line = parser.parse(options, args);
            if (line.hasOption("help") || line.hasOption('?')) {
                printHelp(options);
                return;
            }
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
                isDebug = true;
            }
            if (line.hasOption("cycle")) {
                cycle = true;
            }
            if (line.hasOption("ns")) {
                final Optional<Namespace> optType = Enums.getIfPresent(Namespace.class, line.getOptionValue("ns"));
                if (!optType.isPresent()) {
                    throw new ParseException("Namespace Invalid");
                }
                namespace = optType.get();
            }
            obrIndexFile = line.getOptionValue("o");
            bundleListFile = line.getOptionValue("b");
        } catch (final ParseException exp) {
            logger.info(exp.getMessage());
            printHelp(options);
            System.exit(-1);
        }

        final File obrIndex = IO.getFile(obrIndexFile);
        if (!obrIndex.exists()) {
            logger.info("No OBR Index Found");
            System.exit(-1);
        }

        final File bundlesFile = IO.getFile(bundleListFile);
        if (!bundlesFile.exists()) {
            logger.info("No Bundle List Found");
            System.exit(-1);
        }

        final CliConfiguration config = new CliConfiguration();
        config.isDebug = isDebug;
        config.bundles = bundlesFile;
        config.obrIndex = obrIndex;
        config.checkCycle = cycle;
        config.namespace = namespace;
        config.showEdgeLabel = showEdgeLabel;

        if (isDebug) {
            logger.setLevel(Level.DEBUG);
        }

        logger.debug("Cli Configuration => {} ", config);

        final GraphConfigurer configurer = new GraphConfigurer(config);
        configurer.init();
    }

    private static void printHelp(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Dependency Graph in OSGi - Help", options);
    }

}
