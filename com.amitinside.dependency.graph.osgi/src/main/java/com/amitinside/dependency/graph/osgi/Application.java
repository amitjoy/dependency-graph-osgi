package com.amitinside.dependency.graph.osgi;

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

    private static final boolean SHOW_EDGE_LABEL = true;

    public static void main(final String... args) throws Exception {
        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addOption("o", true, "OBR Index File Location").addOption("b", true, "Bundle List File Location");

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
            obrIndexFile = line.getOptionValue("o");
            bundleListFile = line.getOptionValue("b");
        } catch (final ParseException exp) {
            System.err.println(exp.getMessage());
            System.exit(-1);
        }

        final File file = IO.getFile(obrIndexFile);
        if (!file.exists()) {
            System.out.println("No OBR Index Found");
        }
        final File bundlesFile = IO.getFile(bundleListFile);
        if (!bundlesFile.exists()) {
            System.out.println("No Bundle List Found");
        }

        final Application app = new Application();
        final DependencyGraph dependencyGraph = new DependencyGraph("QIVICON");

        final List<String> bundles = FileUtils.readLines(bundlesFile, "UTF-8");
        final ResourcesRepository repo = app.getRepository(file.toURI());

        bundles.stream().filter(b -> !b.trim().isEmpty()).forEach(b -> {
            //@formatter:off
            final Resource resource = repo.getResources().stream()
                                                   .filter(r -> b.equals(app.getBSN(r)))
                                                   .findFirst()
                                                   .orElse(null);
            //@formatter:on
            final List<ResourceInfo> requiredResources = app.getResourcesRequiredBy(repo, resource);
            app.prepareGraph(dependencyGraph, resource, requiredResources);
        });
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
        if (resource == null) {
            return Collections.emptyList();
        }
        final List<ResourceInfo> resources = Lists.newArrayList();
        resource.getRequirements(null).forEach(r -> {
            final List<Capability> capabilities = repo.findProvider(r);
            final Set<Resource> requiredResources = ResourceUtils.getResources(capabilities);
            final ResourceInfo rInfo = new ResourceInfo();
            rInfo.requirement = r;
            rInfo.requiredResources = requiredResources;
            resources.add(rInfo);
        });
        return resources;
    }

    private void prepareGraph(final DependencyGraph dependencyGraph, final Resource resource,
            final List<ResourceInfo> requiredResources) {
        final String bsn = getBSN(resource);
        if (!dependencyGraph.hasNode(bsn)) {
            dependencyGraph.addNode(bsn);
        }

        // sorting is required since osgi.wiring.package has least priority. That is, if a bundle A uses a service
        // from another bundle B, A not only has osgi.service requirement to B, it also has osgi.wiring.package
        // requirement to B. And if osgi.service is already found, we don't need to show the osgi.wiring.package
        // requirement on the graph since it is a default requirement in this case
        Collections.sort(requiredResources);

        requiredResources.forEach(r -> {
            r.requiredResources.forEach(res -> {
                final String rbsn = getBSN(res);
                if (!dependencyGraph.hasNode(rbsn)) {
                    dependencyGraph.addNode(rbsn);
                }
                final String edgeLabel = StringUtils.substringAfterLast(r.requirement.getNamespace(), ".");
                if (!dependencyGraph.hasEdgeBetween(bsn, rbsn)) {
                    if (SHOW_EDGE_LABEL) {
                        dependencyGraph.addEdge(bsn, rbsn, edgeLabel);
                    } else {
                        dependencyGraph.addEdge(bsn, rbsn);
                    }
                }
            });
        });
    }

}
