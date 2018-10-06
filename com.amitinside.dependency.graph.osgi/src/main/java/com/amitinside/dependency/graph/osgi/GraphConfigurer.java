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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toSet;
import static org.slf4j.Logger.ROOT_LOGGER_NAME;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;
import org.slf4j.LoggerFactory;

import com.amitinside.dependency.graph.osgi.algo.CycleFinderAlgo;
import com.amitinside.dependency.graph.osgi.util.Helper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import aQute.bnd.osgi.repository.ResourcesRepository;
import aQute.bnd.osgi.repository.XMLResourceParser;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.osgi.resource.ResourceUtils.IdentityCapability;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public final class GraphConfigurer {

    private final Logger logger = (Logger) LoggerFactory.getLogger(ROOT_LOGGER_NAME);

    private final DependencyGraph dependencyGraph;
    private final CliConfiguration cliConfiguration;

    public GraphConfigurer(final CliConfiguration cliConfiguration) {
        dependencyGraph = new DependencyGraph("OSGi Dependency Graph");
        this.cliConfiguration = cliConfiguration;

        if (cliConfiguration.isDebug) {
            logger.setLevel(Level.DEBUG);
        }
    }

    public void init() throws Exception {
        final List<String> bundles = FileUtils.readLines(cliConfiguration.bundles, UTF_8);
        final ResourcesRepository repo = getRepository(cliConfiguration.obrIndex.toURI());

        final Set<String> bundlesToPlot = matchWildCards(bundles, repo);

        // plot all base nodes
        bundlesToPlot.forEach(dependencyGraph::addBaseNode);

        for (final String bundle : bundlesToPlot) {
            if (!bundle.trim().isEmpty()) {
                //@formatter:off
                final Resource resource = repo.getResources()
                                              .stream()
                                              .filter(r -> bundle.equals(getBSN(r)))
                                              .findFirst()
                                              .orElse(null);
                //@formatter:on
                if (resource == null) {
                    logger.info("Bundle with BSN [{}] not found", bundle);
                    continue;
                }
                final List<ResourceInfo> requiredResources = getResourcesRequiredBy(repo, resource);
                prepareGraph(dependencyGraph, resource, requiredResources, cliConfiguration.showEdgeLabel);
            }
        }
        if (dependencyGraph.isEmpty()) {
            logger.info("No Element to plot on the Graph");
            System.exit(-1);
        }
        if (cliConfiguration.checkCycle) {
            final CycleFinderAlgo cycleFinderAlgo = new CycleFinderAlgo(cliConfiguration.isDebug);
            cycleFinderAlgo.init(dependencyGraph.internal());
            cycleFinderAlgo.compute();
            logger.info("Existence of Cycle => {}", cycleFinderAlgo.hasCycle());
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

    private List<ResourceInfo> getResourcesRequiredBy(final ResourcesRepository repo, final Resource resource) {
        logger.debug("OSGi Resource to search => {} ", resource);
        if (resource == null) {
            return Collections.emptyList();
        }
        final List<ResourceInfo> resources = Lists.newArrayList();
        resource.getRequirements(null).forEach(r -> {
            logger.debug("Resource Requirement => {}", r);
            final List<Capability> capabilities = repo.findProvider(r);
            final Set<Resource> requiredResources = ResourceUtils.getResources(capabilities);
            logger.debug("Resources providing the Requirement => {}", requiredResources);
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

        requiredResources.stream().filter(this::checkNamespace).forEach(r -> r.requiredResources.forEach(res -> {
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

    private boolean checkNamespace(final ResourceInfo r) {
        switch (cliConfiguration.namespace) {
            case ALL:
                return true;
            case CUSTOM:
                return cliConfiguration.customNamespace.equals(r.requirement.getNamespace());
            default:
                return cliConfiguration.namespace.ns().equals(r.requirement.getNamespace());
        }
    }

    private Set<String> matchWildCards(final List<String> input, final ResourcesRepository repo) {
        final Predicate<? super String> hasWildcard = e -> e.endsWith("*") || e.endsWith("?");
        final Set<String> wildcardEntries = input.stream().filter(hasWildcard).collect(toSet());

        final Set<String> bundles = Sets.newHashSet(input);
        bundles.removeAll(wildcardEntries);

        for (final String wildcardEntry : wildcardEntries) {
            for (final Resource resource : repo.getResources()) {
                final String bsn = getBSN(resource);
                if (Helper.isMatch(bsn, wildcardEntry)) {
                    bundles.add(bsn);
                }
            }
        }
        return bundles;
    }

}
