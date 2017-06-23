package org.nextprot.api.core.utils.graph;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import grph.path.Path;

import java.util.Collection;

/**
 * A directed graph of int nodes and edges
 *
 * Created by fnikitin on 23.06.17.
 */
public class CvTermGraph implements DirectedGraph {

    private static int nextElementAvailable = 0;

    private final TIntSet nodes = new TIntHashSet();
    private final TIntSet edges = new TIntHashSet();
    private final TIntList tails = new TIntArrayList();
    private final TIntList heads = new TIntArrayList();
    private final TIntObjectMap<TIntSet> adjacencyLists = new TIntObjectHashMap<>();

    @Override
    public void addNode(int node) {

        nodes.add(node);
    }

    @Override
    public int addEdge(int tail, int head) {

        if (edges.contains(nextElementAvailable)) {
            return nextElementAvailable;
        }

        if (!containsNode(tail)) {
            addNode(tail);
        }

        if (!containsNode(head)) {
            addNode(head);
        }

        if (!adjacencyLists.containsKey(tail)) {
            adjacencyLists.put(tail, new TIntHashSet());
        }

        if (!adjacencyLists.get(tail).contains(head)) {
            adjacencyLists.get(tail).add(head);

            tails.add(tail);
            heads.add(head);

            edges.add(nextElementAvailable++);

            return nextElementAvailable-1;
        }

        throw new IllegalStateException("already existing edge: "+tail+ " -> "+head);
    }

    @Override
    public int[] getNodes() {

        return nodes.toArray();
    }

    @Override
    public int[] getEdges() {

        return edges.toArray();
    }

    @Override
    public int getTailNode(int edge) {

        if (edge >= 0 && edge < tails.size()) {

            return tails.get(edge);
        }
        return -1;
    }

    @Override
    public int getHeadNode(int edge) {

        if (edge >= 0 && edge < heads.size()) {

            return heads.get(edge);
        }
        return -1;
    }

    @Override
    public boolean containsNode(int node) {

        return nodes.contains(node);
    }

    @Override
    public boolean containsEdge(int edge) {

        return edges.contains(edge);
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
