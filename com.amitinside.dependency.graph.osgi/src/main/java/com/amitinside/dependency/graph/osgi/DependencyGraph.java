package com.amitinside.dependency.graph.osgi;

import java.util.UUID;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

public final class DependencyGraph {

    private final Graph graph;

    public DependencyGraph(final String name) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        graph = new MultiGraph(name);
        final Viewer viewer = graph.display();
        viewer.enableAutoLayout();
    }

    public void addNode(final String bsn) {
        final String shortenedBSN = shortenBSN(bsn);
        final Node node = graph.addNode(shortenedBSN);
        node.addAttribute("ui.style",
                "text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 5px, 4px; text-offset: 0px, 5px; ");
        node.addAttribute("ui.label", shortenedBSN);
        node.addAttribute("ui.quality");
        node.addAttribute("ui.antialias");
    }

    public void removeNode(final String bsn) {
        graph.removeNode(bsn);
    }

    public void addEdge(final String source, final String dependsOn, final String edgeLabel) {
        final Edge edge = graph.addEdge(UUID.randomUUID().toString(), shortenBSN(source), shortenBSN(dependsOn), true);
        edge.addAttribute("ui.label", edgeLabel);
    }

    public void addEdge(final String source, final String dependsOn) {
        graph.addEdge(UUID.randomUUID().toString(), shortenBSN(source), shortenBSN(dependsOn), true);
    }

    public boolean hasNode(final String bsn) {
        return graph.getNode(shortenBSN(bsn)) != null;
    }

    public boolean hasEdgeBetween(final String bsn1, final String bsn2) {
        return graph.getNode(shortenBSN(bsn1)).hasEdgeBetween(shortenBSN(bsn2));
    }

    public void display() {
        graph.display();
    }

    private String shortenBSN(final String bsn) {
        if (bsn.length() > 15 && bsn.indexOf('.') != -1) {
            final String[] parts = bsn.split("\\.");
            if (parts.length > 2) {
                final StringBuilder builder = new StringBuilder();
                builder.append(parts[0].charAt(0));
                builder.append(".");
                builder.append(parts[1].charAt(0));
                builder.append(".");
                for (int i = 2; i < parts.length; i++) {
                    builder.append(parts[i]);
                    if (i != parts.length - 1) {
                        builder.append(".");
                    }
                }
                return builder.toString();
            }
        }
        return bsn;
    }

}
