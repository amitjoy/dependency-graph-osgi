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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

import com.google.common.collect.Lists;

import aQute.bnd.osgi.repository.ResourcesRepository;
import aQute.bnd.osgi.repository.XMLResourceParser;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.osgi.resource.ResourceUtils.IdentityCapability;
import aQute.lib.io.IO;

public final class Application {

    public static void main(final String... args) throws Exception {

        boolean showEdgeLabel = false;
        boolean debug = false;

        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addOption("o", true, "OBR Index File Location");
        options.addOption("b", true, "Bundle List File Location");
        options.addOption("e", false, "Show Edge Labels");
        options.addOption("debug", false, "Turn on Debug Mode");

        String obrIndexFile = null;
        String bundleListFile = null;
        try {
            final CommandLine line = parser.parse(options, args);
            if (!line.hasOption("o")) {
                throw new ParseException("OBR Index File must be used");
            }
            if (!line.hasOption("b")) {
                throw new ParseException("Bundle List File must be used");
            }
            if (line.hasOption("e")) {
                showEdgeLabel = true;
            }
            if (line.hasOption("debug")) {
                debug = true;
            }
            obrIndexFile = line.getOptionValue("o");
            bundleListFile = line.getOptionValue("b");
        } catch (final ParseException exp) {
            System.err.println(exp.getMessage());
            System.exit(-1);
        }

        final File file = IO.getFile(obrIndexFile);
        if (!file.exists()) {
            System.out.println("No OBR Index Found");
            System.exit(-1);
        }
        final File bundlesFile = IO.getFile(bundleListFile);
        if (!bundlesFile.exists()) {
            System.out.println("No Bundle List Found");
            System.exit(-1);
        }

        final Application app = new Application();
        final DependencyGraph dependencyGraph = new DependencyGraph("OSGi Dependency Graph");

        final List<String> bundles = FileUtils.readLines(bundlesFile, UTF_8);
        final ResourcesRepository repo = app.getRepository(file.toURI());

        // plot all base nodes
        bundles.forEach(dependencyGraph::addBaseNode);

        for (final String bundle : bundles) {
            if (!bundle.trim().isEmpty()) {
                //@formatter:off
                final Resource resource = repo.getResources()
                                                       .stream()
                                                       .filter(r -> bundle.equals(app.getBSN(r)))
                                                       .findFirst()
                                                       .orElse(null);
                //@formatter:on
                if (resource == null) {
                    System.out.println("Bundle with BSN [" + bundle + "] not found");
                    continue;
                }
                final List<ResourceInfo> requiredResources = app.getResourcesRequiredBy(repo, resource, debug);
                app.prepareGraph(dependencyGraph, resource, requiredResources, showEdgeLabel);
            }
        }
        if (dependencyGraph.isEmpty()) {
            System.out.println("No Element to plot on the Graph");
            System.exit(-1);
        }
        dependencyGraph.display();
    }

    private ResourcesRepository getRepository(final URI uri) throws Exception {
        final List<Resource> resources = XMLResourceParser.getResources(uri);
        return new ResourcesRepository(resources);
    }

    private String getBSN(final Resource resource) {
        final IdentityCapability identity = ResourceUtils.getIdentityCapability(resource);
        return identity.getAttributes().get("osgi.identity").toString();
    }

    private List<ResourceInfo> getResourcesRequiredBy(final ResourcesRepository repo, final Resource resource,
            final boolean debug) {
        if (debug) {
            System.out.println("OSGi Resource to search => " + resource);
        }
        if (resource == null) {
            return Collections.emptyList();
        }
        final List<ResourceInfo> resources = Lists.newArrayList();
        resource.getRequirements(null).forEach(r -> {
            if (debug) {
                System.out.println("Resource Requirement => " + r);
            }
            final List<Capability> capabilities = repo.findProvider(r);
            final Set<Resource> requiredResources = ResourceUtils.getResources(capabilities);
            if (debug) {
                System.out.println("Resources providing the Requirement => " + requiredResources);
            }
            final ResourceInfo rInfo = new ResourceInfo();
            rInfo.requirement = r;
            rInfo.requiredResources = requiredResources;
            resources.add(rInfo);
        });
        return resources;
    }

    private void prepareGraph(final DependencyGraph dependencyGraph, final Resource resource,
            final List<ResourceInfo> requiredResources, final boolean showEdgeLabel) {
        if (resource == null) {
            return;
        }
        final String bsn = getBSN(resource);

        // sorting is required since osgi.wiring.package has least priority. That is, if a bundle A uses a service
        // from another bundle B, A not only has osgi.service requirement to B, it also has osgi.wiring.package
        // requirement to B. And if osgi.service is already found, we don't need to show the osgi.wiring.package
        // requirement on the graph since it is a default requirement in this case
        Collections.sort(requiredResources);

        requiredResources.forEach(r -> r.requiredResources.forEach(res -> {
            final String rbsn = getBSN(res);
            if (!dependencyGraph.hasNode(rbsn)) {
                dependencyGraph.addNode(rbsn);
            }
            final String edgeLabel = StringUtils.substringAfterLast(r.requirement.getNamespace(), ".");
            if (!dependencyGraph.hasEdgeBetween(bsn, rbsn)) {
                if (showEdgeLabel) {
                    dependencyGraph.addEdge(bsn, rbsn, edgeLabel);
                } else {
                    dependencyGraph.addEdge(bsn, rbsn);
                }
            }
        }));
    }

}
