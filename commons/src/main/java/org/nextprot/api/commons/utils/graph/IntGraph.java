package org.nextprot.api.commons.utils.graph;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import toools.collections.Arrays;

import java.util.stream.IntStream;

/**
 * A directed graph of int nodes and edges
 *
 * Created by fnikitin on 23.06.17.
 */
public class IntGraph implements DirectedGraph {

    private final TIntList nodes = new TIntArrayList();
    private final TIntList tails = new TIntArrayList();
    private final TIntList heads = new TIntArrayList();
    private final TIntObjectMap<TIntSet> predecessorLists = new TIntObjectHashMap<>();
    private final TIntObjectMap<TIntSet> successorLists = new TIntObjectHashMap<>();

    @Override
    public void addNode(int node) {

        assert !nodes.contains(node) : node;

        nodes.add(node);
    }

    @Override
    public int addEdge(int tail, int head) {

        if (!containsNode(tail)) {
            addNode(tail);
        }

        if (!containsNode(head)) {
            addNode(head);
        }

        boolean created = addSuccessor(tail, head);

        if (created) {
            addPredecessor(tail, head);

            tails.add(tail);
            heads.add(head);

            return tails.size()-1;
        }

        throw new IllegalStateException("already existing edge: "+tail+ " -> "+head);
    }

    private boolean addSuccessor(int tail, int head) {

        if (!successorLists.containsKey(tail)) {
            successorLists.put(tail, new TIntHashSet());
        }

        if (!successorLists.get(tail).contains(head)) {
            successorLists.get(tail).add(head);

            return true;
        }

        return false;
    }

    private boolean addPredecessor(int tail, int head) {

        if (!predecessorLists.containsKey(head)) {
            predecessorLists.put(head, new TIntHashSet());
        }

        if (!predecessorLists.get(head).contains(tail)) {
            predecessorLists.get(head).add(tail);

            return true;
        }

        return false;
    }

    @Override
    public int[] getNodes() {

        return nodes.toArray();
    }

    @Override
    public int[] getEdges() {

        return IntStream.range(0, tails.size()).toArray();
    }

    @Override
    public int[] getEdgesIncidentTo(int... nodes) {

        TIntSet edges = new TIntHashSet();

        edges.addAll(getInEdges(nodes));
        edges.addAll(getOutEdges(nodes));

        return edges.toArray();
    }

    @Override
    public int[] getInEdges(int... nodes) {

        TIntSet edges = new TIntHashSet();

        for (int node : nodes) {

            findMatchingPointEdges(node, heads, edges);
        }

        return edges.toArray();
    }

    @Override
    public int[] getOutEdges(int... nodes) {

        TIntSet edges = new TIntHashSet();

        for (int node : nodes) {

            findMatchingPointEdges(node, tails, edges);
        }

        return edges.toArray();
    }

    private TIntSet findMatchingPointEdges(int node, TIntList endPoints, TIntSet edges) {

        for (int i=0 ; i<endPoints.size() ; i++) {

            if (endPoints.get(i) == node) {
                edges.add(i);
            }
        }

        return edges;
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

        return edge < tails.size();
    }

    @Override
    public boolean containsEdge(int tail, int head) {

        return successorLists.containsKey(tail) && successorLists.get(tail).contains(head);
    }

    // TODO optimisation: could compute once and store all ancestors in map of nodes to ancestor nodes
    @Override
    public int[] getAncestors(int node) {

        TIntSet ancestors = new TIntHashSet();

        getAncestors(node, ancestors);

        return ancestors.toArray();
    }

    private void getAncestors(int node, TIntSet ancestors) {

        if (!predecessorLists.containsKey(node)) {
            return;
        }

        predecessorLists.get(node).forEach(predecessor -> {

            ancestors.add(predecessor);
            getAncestors(predecessor, ancestors);

            return true;
        });
    }

    @Override
    public boolean isAncestorOf(int queryAncestor, int queryDescendant) {

        return Arrays.contains(getAncestors(queryDescendant), queryAncestor);
    }

    @Override
    public int[] getPredecessors(int node) {

        if (!predecessorLists.containsKey(node)) {
            return new int[0];
        }

        return predecessorLists.get(node).toArray();
    }

    @Override
    public int[] getSuccessors(int node) {

        if (!successorLists.containsKey(node)) {
            return new int[0];
        }

        return successorLists.get(node).toArray();
    }

    @Override
    public int getInDegree(int node) {

        return getPredecessors(node).length;
    }

    @Override
    public int getOutDegree(int node) {

        return getSuccessors(node).length;
    }

    @Override
    public int[] getSources() {

        TIntSet sources = new TIntHashSet();

        for (int node : getNodes()) {

            if (isSource(node)) {
                sources.add(node);
            }
        }

        return sources.toArray();
    }

    @Override
    public int[] getSinks() {

        TIntSet sinks = new TIntHashSet();

        for (int node : getNodes()) {

            if (isSink(node)) {
                sinks.add(node);
            }
        }

        return sinks.toArray();
    }

    @Override
    public DirectedGraph calcAncestorSubgraph(int node) {

        int[] ancestors = getAncestors(node);

        IntGraph sg = new IntGraph();

        sg.addNode(node);
        for (int i=0 ; i<ancestors.length ; i++) {
             sg.addNode(ancestors[i]);
        }

        int[] edges = getInEdges(sg.getNodes());

        for (int i=0; i < edges.length ; i++) {
            sg.addEdge(getTailNode(edges[i]), getHeadNode(edges[i]));
        }

        return sg;
    }
}
