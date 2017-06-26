package org.nextprot.api.core.utils.graph;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;

import java.util.Arrays;
import java.util.Collection;

public class CvTermGrph implements DirectedGraph {

    private final Grph graph;

    public CvTermGrph() {

        graph = new InMemoryGrph();

    }

    @Override
    public void addNode(int node) {

        graph.addVertex(node);
    }

    @Override
    public int addEdge(int tail, int head) {

        return (int) graph.addDirectedSimpleEdge(tail, head);
    }

    @Override
    public int[] getNodes() {

        return Arrays.stream(graph.getVertices().toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int[] getEdges() {

        return Arrays.stream(graph.getEdges().toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int getTailNode(int edge) {

        return (int)graph.getVerticesIncidentToEdge(edge).toLongArray()[0];
    }

    @Override
    public int getHeadNode(int edge) {

        return (int)graph.getVerticesIncidentToEdge(edge).toLongArray()[1];
    }

    @Override
    public boolean containsNode(int node) {

        return graph.containsVertex(node);
    }

    @Override
    public boolean containsEdge(int edge) {

        return graph.containsVertex(edge);
    }

    @Override
    public int[] getAncestors(int cvTermId) {
        return new int[0];
    }

    @Override
    public boolean isAncestorOf(int queryAncestor, int queryDescendant) {
        return false;
    }

    @Override
    public Collection<Path> calcAllPaths() {
        return null;
    }

    @Override
    public DirectedGraph calcAncestorSubgraph(int node) {
        return null;
    }
}
