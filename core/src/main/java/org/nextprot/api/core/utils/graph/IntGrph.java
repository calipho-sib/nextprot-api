package org.nextprot.api.core.utils.graph;

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

    private final Grph graph;
    private final Map<Integer, LongSet> ancestors;
    private boolean allAncestorComputed = false;

    public IntGrph() {

        this(new InMemoryGrph());
    }

    public IntGrph(Grph graph) {

        this.graph = graph;
        ancestors = new HashMap<>();
    }

    @Override
    public void addNode(int node) {

        graph.addVertex(node);
        ancestors.put(node, new LongHashSet());
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
    public boolean containsEdge(int tail, int head) {
        return false;
    }

    public void computeAllAncestors() {

        Collection<Path> paths = graph.getAllPaths();

        for (Path path : paths) {

            System.out.println(path);
            int dest = (int) path.getDestination();
            System.out.println(dest);
            if (path.getNumberOfVertices() > 1) {
                for (int i = 0; i < path.getNumberOfVertices() - 1; i++) {

                    System.out.println(" -> "+path.getVertexAt(i));
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
        return new int[0];
    }

    @Override
    public int[] getOutEdges(int... nodes) {
        return new int[0];
    }

    @Override
    public DirectedGraph calcAncestorSubgraph(int node) {

        LongSet ancestors = new LongHashSet();
        ancestors.add(node);
        ancestors.addAll(this.ancestors.get(node));
        return new IntGrph(graph.getSubgraphInducedByVertices(ancestors));
    }
}
