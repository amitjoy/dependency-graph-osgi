package com.amitinside.dependency.graph.osgi.algo;

import java.util.List;
import java.util.Set;

import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import com.amitinside.dependency.graph.osgi.DependencyGraph;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CycleFinderAlgo {

    private final boolean debug;
    private final Graph graph;
    private final TarjanStronglyConnectedComponents tscc;

    public CycleFinderAlgo(final DependencyGraph graph, final boolean debug) {
        this.debug = debug;
        this.graph = graph.internal();
        tscc = new TarjanStronglyConnectedComponents();
    }

    public void compute() {
        tscc.init(graph);
        tscc.compute();
    }

    public boolean hasCycle() {
        final List<Integer> sccIndices = Lists.newArrayList();
        for (final Node n : graph.getEachNode()) {
            final Integer attribute = n.getAttribute(tscc.getSCCIndexAttribute(), Integer.class);
            if (debug) {
                n.addAttribute("label", attribute);
            }
            sccIndices.add(attribute);
        }
        final Set<Integer> set = Sets.newHashSet(sccIndices);
        return sccIndices.size() != set.size();
    }

}
