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

public class CvTermGrph implements DirectedGraph {

    private final Grph graph;
    private final Map<Integer, LongSet> cvTermIdAncestors;
    private boolean allAncestorComputed = false;

    public CvTermGrph() {

        graph = new InMemoryGrph();
        cvTermIdAncestors = new HashMap<>();
    }

    @Override
    public void addNode(int node) {

        graph.addVertex(node);
        cvTermIdAncestors.put(node, new LongHashSet());
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

    public void computeAllAncestors() {

        Collection<Path> paths = graph.getAllPaths();

        for (Path path : paths) {

            long dest = path.getDestination();

            if (path.getNumberOfVertices() > 1) {
                for (long i = 0; i < path.getNumberOfVertices() - 1; i++) {

                    cvTermIdAncestors.get(dest).add(path.getVertexAt(i));
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
        return Arrays.stream(cvTermIdAncestors.get(cvTermId).toLongArray()).mapToInt(l -> (int)l).toArray();
    }

    @Override
    public boolean isAncestorOf(int queryAncestor, int queryDescendant) {

        return cvTermIdAncestors.get(queryDescendant).contains(queryAncestor);
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
