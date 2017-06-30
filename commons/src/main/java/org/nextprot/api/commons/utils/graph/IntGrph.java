package org.nextprot.api.commons.utils.graph;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;
import toools.collection.bigstuff.longset.LongHashSet;
import toools.collection.bigstuff.longset.LongSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IntGrph implements DirectedGraph {

    private String graphLabel;
    private final Grph graph;
    private final Map<Integer, LongSet> ancestors;
    private final Map<Integer, String> nodeLabels;
    private final Map<Integer, String> edgeLabels;

    private boolean allAncestorComputed = false;

    public IntGrph() {

        this(new InMemoryGrph());
    }

    public IntGrph(Grph graph) {

        this.graph = graph;

        ancestors = new HashMap<>();
        nodeLabels = new HashMap<>();
        edgeLabels = new HashMap<>();
    }

    @Override
    public void setGraphLabel(String label) {

        this.graphLabel = label;
    }

    @Override
    public String getGraphLabel() {

        return graphLabel;
    }

    @Override
    public void addNode(int node) {

        graph.addVertex(node);
        ancestors.put(node, new LongHashSet());
    }

    @Override
    public void setNodeLabel(int node, String label) {

        if (!containsNode(node)) {
            throw new IllegalArgumentException("node " + node+" does not exist");
        }

        nodeLabels.put(node, label);
    }

    @Override
    public String getNodeLabel(int node) {

        return nodeLabels.get(node);
    }

    @Override
    public int addEdge(int tail, int head) {

        if (!containsNode(tail)) {
            addNode(tail);
        }

        if (!containsNode(head)) {
            addNode(head);
        }

        return (int) graph.addDirectedSimpleEdge(tail, head);
    }

    @Override
    public void setEdgeLabel(int edge, String label) {

        edgeLabels.put(edge, label);
    }

    @Override
    public String getEdgeLabel(int edge) {

        return edgeLabels.get(edge);
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
    public int getEdge(int tail, int head) {

        if (!containsNode(tail) || !containsNode(head)) {
            return -1;
        }

        LongSet edges = graph.getEdgesConnecting(tail, head);

        if (edges.isEmpty()) {
            return -1;
        }
        if (edges.size() > 1) {
            throw new IllegalStateException("node "+tail +" should be have a direct connection with node "+head);
        }
        return (int) edges.toLongArray()[0];
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
    public boolean containsEdge(int tail, int head) {

        int[] edges = getOutEdges(tail);

        for (int edge : edges) {

            if (getHeadNode(edge) == head) {
                return true;
            }
        }

        return false;
    }

    public void computeAllAncestors() {

        Collection<Path> paths = graph.getAllPaths();

        for (Path path : paths) {

            int dest = (int) path.getDestination();
            if (path.getNumberOfVertices() > 1) {
                for (int i = 0; i < path.getNumberOfVertices() - 1; i++) {

                    ancestors.get(dest).add(path.getVertexAt(i));
                }
            }
        }
    }

    @Override
    public int[] getAncestors(int cvTermId) {

        if (!allAncestorComputed) {
            computeAllAncestors();
            allAncestorComputed = true;
        }
        return Arrays.stream(ancestors.get(cvTermId).toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public boolean isAncestorOf(int queryAncestor, int queryDescendant) {

        if (!allAncestorComputed) {
            computeAllAncestors();
            allAncestorComputed = true;
        }

        return ancestors.get(queryDescendant).contains(queryAncestor);
    }

    @Override
    public int[] getPredecessors(int node) {

        return Arrays.stream(graph.getInNeighbors(node).toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int[] getSuccessors(int node) {

        return Arrays.stream(graph.getOutNeighbors(node).toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int getInDegree(int node) {

        return (int) graph.getInVertexDegree(node);
    }

    @Override
    public int getOutDegree(int node) {

        return (int) graph.getOutVertexDegree(node);
    }

    @Override
    public int[] getSources() {
        return Arrays.stream(graph.getSources().toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int[] getSinks() {
        return Arrays.stream(graph.getSinks().toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int[] getEdgesIncidentTo(int... nodes) {

        return Arrays.stream(graph.getEdgesIncidentTo(nodes[0]).toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int[] getInEdges(int... nodes) {

        LongSet edges = new LongHashSet();

        for (int node : nodes) {
            edges.addAll(graph.getInEdges((long)node));
        }

        return Arrays.stream(edges.toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public int[] getOutEdges(int... nodes) {

        LongSet edges = new LongHashSet();

        for (int node : nodes) {
            edges.addAll(graph.getOutEdges((long)node));
        }

        return Arrays.stream(edges.toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public IntGrph calcAncestorSubgraph(int node) {

        LongSet ancestors = new LongHashSet();
        ancestors.add(node);
        ancestors.addAll(getAncestors(node));
        return new IntGrph(graph.getSubgraphInducedByVertices(ancestors));
    }
}
