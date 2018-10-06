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

import static com.amitinside.dependency.graph.osgi.CliOptions.BUNDLE_FILE;
import static com.amitinside.dependency.graph.osgi.CliOptions.CYCLE;
import static com.amitinside.dependency.graph.osgi.CliOptions.DEBUG;
import static com.amitinside.dependency.graph.osgi.CliOptions.HELP_1;
import static com.amitinside.dependency.graph.osgi.CliOptions.HELP_2;
import static com.amitinside.dependency.graph.osgi.CliOptions.NAMESPACE;
import static com.amitinside.dependency.graph.osgi.CliOptions.NAMESPACE_CUSTOM;
import static com.amitinside.dependency.graph.osgi.CliOptions.OBR_FILE;
import static com.amitinside.dependency.graph.osgi.CliOptions.SHOW_EDGE;
import static com.amitinside.dependency.graph.osgi.Namespace.ALL;
import static com.amitinside.dependency.graph.osgi.Namespace.CUSTOM;
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
        Namespace namespace = ALL;
        String customNamespace = null;

        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addOption(HELP_1, false, "Show Help");
        options.addOption(HELP_2, false, "Show Help");
        options.addOption(OBR_FILE, true, "OBR Index File Location");
        options.addOption(BUNDLE_FILE, true, "Bundle List File Location");
        options.addOption(SHOW_EDGE, false, "Show Edge Labels");
        options.addOption(DEBUG, false, "Turn on Debug Mode");
        options.addOption(CYCLE, false, "Check for Cycle Existence");
        final String description = "Namespace Type to Plot " + Lists.newArrayList(Namespace.values())
                + " (Default ALL)";
        options.addOption(NAMESPACE, true, description);
        options.addOption(NAMESPACE_CUSTOM, true, "Custom Namespace (Needs to be set if ns option is set to CUSTOM");

        String obrIndexFile = null;
        String bundleListFile = null;
        try {
            final CommandLine line = parser.parse(options, args);
            if (line.hasOption(HELP_2) || line.hasOption(HELP_1)) {
                printHelp(options);
                return;
            }
            if (!line.hasOption(OBR_FILE)) {
                throw new ParseException("OBR Index File is missing");
            }
            if (!line.hasOption(BUNDLE_FILE)) {
                throw new ParseException("Bundle List File is missing");
            }
            if (line.hasOption(SHOW_EDGE)) {
                showEdgeLabel = true;
            }
            if (line.hasOption(DEBUG)) {
                isDebug = true;
            }
            if (line.hasOption(CYCLE)) {
                cycle = true;
            }
            if (line.hasOption(NAMESPACE)) {
                final Optional<Namespace> optType = Enums.getIfPresent(Namespace.class, line.getOptionValue(NAMESPACE));
                if (!optType.isPresent()) {
                    throw new ParseException("Namespace Invalid");
                }
                namespace = optType.get();
                if (namespace == CUSTOM) {
                    if (!line.hasOption(NAMESPACE_CUSTOM)) {
                        throw new ParseException("ns-custom option must be set if ns is set to CUSTOM");
                    }
                    customNamespace = line.getOptionValue(NAMESPACE_CUSTOM);
                }
            }
            obrIndexFile = line.getOptionValue(OBR_FILE);
            bundleListFile = line.getOptionValue(BUNDLE_FILE);
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
        config.customNamespace = customNamespace;

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
