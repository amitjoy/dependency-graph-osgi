package com.amitinside.dependency.graph.osgi;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    private static final String QUERY = "com.qivicon.runtime.hal";
    private static final boolean SHOW_EDGE_LABEL = true;

    public static void main(final String... args) throws Exception {
        final Application app = new Application();

        final File file = IO.getFile("index.xml");
        if (!file.exists()) {
            System.out.println("No OBR Index Found");
        }
        final ResourcesRepository repo = app.getRepository(file.toURI());

        //@formatter:off
        final Resource resource = repo.getResources().stream()
                                               .filter(r -> QUERY.equals(app.getBSN(r)))
                                               .findFirst()
                                               .orElse(null);
        //@formatter:on
        final List<ResourceInfo> requiredResources = app.getResourcesRequiredBy(repo, resource);
        app.displayGraph(resource, requiredResources);
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

    private void displayGraph(final Resource resource, final List<ResourceInfo> requiredResources) {
        final String bsn = getBSN(resource);
        final DependencyGraph dependencyGraph = new DependencyGraph(bsn);
        dependencyGraph.addNode(bsn);

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
        dependencyGraph.display();
    }

}
