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

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

import com.amitinside.dependency.graph.osgi.algo.CycleFinderAlgo;
import com.google.common.collect.Lists;

import aQute.bnd.osgi.repository.ResourcesRepository;
import aQute.bnd.osgi.repository.XMLResourceParser;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.osgi.resource.ResourceUtils.IdentityCapability;

public final class GraphConfigurer {

    private final DependencyGraph dependencyGraph;
    private final CliConfiguration cliConfiguration;

    public GraphConfigurer(final CliConfiguration cliConfiguration) {
        dependencyGraph = new DependencyGraph("OSGi Dependency Graph");
        this.cliConfiguration = cliConfiguration;
    }

    public void init() throws Exception {
        final List<String> bundles = FileUtils.readLines(cliConfiguration.bundles, UTF_8);
        final ResourcesRepository repo = getRepository(cliConfiguration.obrIndex.toURI());

        // plot all base nodes
        bundles.forEach(dependencyGraph::addBaseNode);

        for (final String bundle : bundles) {
            if (!bundle.trim().isEmpty()) {
                //@formatter:off
                final Resource resource = repo.getResources()
                                              .stream()
                                              .filter(r -> bundle.equals(getBSN(r)))
                                              .findFirst()
                                              .orElse(null);
                //@formatter:on
                if (resource == null) {
                    System.out.println("Bundle with BSN [" + bundle + "] not found");
                    continue;
                }
                final List<ResourceInfo> requiredResources = getResourcesRequiredBy(repo, resource,
                        cliConfiguration.isDebug);
                prepareGraph(dependencyGraph, resource, requiredResources, cliConfiguration.showEdgeLabel);
            }
        }
        if (dependencyGraph.isEmpty()) {
            System.out.println("No Element to plot on the Graph");
            System.exit(-1);
        }
        if (cliConfiguration.checkCycle) {
            final CycleFinderAlgo cycleFinderAlgo = new CycleFinderAlgo(cliConfiguration.isDebug);
            cycleFinderAlgo.init(dependencyGraph.internal());
            cycleFinderAlgo.compute();
            System.err.println("Existence of Cycle => " + cycleFinderAlgo.hasCycle());
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

        requiredResources.stream().filter(this::checkRequirement).forEach(r -> r.requiredResources.forEach(res -> {
            final String rbsn = getBSN(res);
            if (!dependencyGraph.hasNode(rbsn)) {
                dependencyGraph.addNode(rbsn);
            }
            final String edgeLabel = StringUtils.substringAfterLast(r.requirement.getNamespace(), ".");
            if (!dependencyGraph.hasEdgeBetween(bsn, rbsn) && !bsn.equalsIgnoreCase(rbsn)) {
                if (showEdgeLabel) {
                    dependencyGraph.addEdge(bsn, rbsn, edgeLabel);
                } else {
                    dependencyGraph.addEdge(bsn, rbsn);
                }
            }
        }));
    }

    private boolean checkRequirement(final ResourceInfo r) {
        if (!cliConfiguration.checkOnlyService) {
            return true;
        }
        return r.requirement.getNamespace().equals("osgi.service");
    }

}
