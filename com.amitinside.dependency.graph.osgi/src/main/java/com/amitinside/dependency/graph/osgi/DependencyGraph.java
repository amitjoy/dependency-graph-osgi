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

import java.util.UUID;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

public final class DependencyGraph {

    private static final int MAX_LENGTH_FOR_SHORTENING = 15;
    private final Graph graph;

    public DependencyGraph(final String name) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        graph = Graphs.synchronizedGraph(new MultiGraph(name));
        graph.addAttribute("ui.quality");
    }

    public void addBaseNode(final String bsn) {
        addNode(bsn, true);
    }

    public void addNode(final String bsn) {
        addNode(bsn, false);
    }

    public void addNode(final String bsn, final boolean isBase) {
        final String shortenedBSN = shortenBSN(bsn);
        final Node node = graph.addNode(shortenedBSN);
        if (!isBase) {
            node.addAttribute("ui.style",
                    "text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: #222C; text-padding: 5px, 4px; text-offset: 0px, 5px; ");
        } else {
            node.addAttribute("ui.style",
                    "text-alignment: under; text-color: white; text-style: bold; text-background-mode: rounded-box; text-background-color: red; text-padding: 5px, 4px; text-offset: 0px, 5px; ");
        }

        node.addAttribute("ui.label", shortenedBSN);
        node.addAttribute("ui.quality");
        node.addAttribute("ui.antialias");
    }

    public void removeNode(final String bsn) {
        graph.removeNode(shortenBSN(bsn));
    }

    public void addEdge(final String source, final String dependsOn, final String edgeLabel, final boolean showLabel) {
        final Edge edge = graph.addEdge(UUID.randomUUID().toString(), shortenBSN(source), shortenBSN(dependsOn), true);
        edge.addAttribute("ui.style", "shape: freeplane;");
        if (showLabel) {
            edge.addAttribute("ui.label", edgeLabel);
        }
    }

    public void addEdge(final String source, final String dependsOn, final String edgeLabel) {
        addEdge(source, dependsOn, edgeLabel, true);
    }

    public void addEdge(final String source, final String dependsOn) {
        addEdge(source, dependsOn, null, false);
    }

    public boolean hasNode(final String bsn) {
        return graph.getNode(shortenBSN(bsn)) != null;
    }

    public boolean hasEdgeBetween(final String bsn1, final String bsn2) {
        return graph.getNode(shortenBSN(bsn1)).hasEdgeBetween(shortenBSN(bsn2));
    }

    public void display() {
        final Viewer viewer = graph.display();
        viewer.enableAutoLayout();
    }

    public boolean isEmpty() {
        return graph.getNodeSet().isEmpty();
    }

    public int getNodeCount() {
        return graph.getNodeCount();
    }

    public Graph internal() {
        return graph;
    }

    private String shortenBSN(final String bsn) {
        if (bsn.length() > MAX_LENGTH_FOR_SHORTENING && bsn.indexOf('.') != -1) {
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
