package org.nextprot.api.commons.utils.graph;

import com.google.common.base.Preconditions;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A directed graph of int nodes and edges
 *
 * Created by fnikitin on 23.06.17.
 */
public class IntGraph implements DirectedGraph, Externalizable {

    private String graphLabel;
    private TIntList nodes = new TIntArrayList();
    private TIntList tails = new TIntArrayList();
    private TIntList heads = new TIntArrayList();
    private TIntObjectMap<TIntSet> predecessorLists = new TIntObjectHashMap<>();
    private TIntObjectMap<TIntSet> successorLists = new TIntObjectHashMap<>();
    private TIntObjectMap<Map<String, String>> nodeMetadata = new TIntObjectHashMap<>();
    private TObjectIntMap<String> nodesByMetadataValue = new TObjectIntHashMap<>();
    private TIntObjectMap<String> edgeLabels = new TIntObjectHashMap<>();

    public IntGraph() {

        this("");
    }

    public IntGraph(String label) {

        setGraphLabel(label);
    }

    @Override
    public void setGraphLabel(String label) {

        Preconditions.checkNotNull(label, "graph label should be defined");
        this.graphLabel = label;
    }

    @Override
    public String getGraphLabel() {

        return graphLabel;
    }

    @Override
    public void addNodeMetadata(int node, String key, String value) {

        if (!containsNode(node)) {
            throw new IllegalArgumentException("node " + node+" does not exist");
        }

        if (!nodeMetadata.containsKey(node)) {
            nodeMetadata.put(node, new HashMap<>());
        }

        if (nodeMetadata.get(node).containsKey(key)) {
            throw new IllegalArgumentException("node metadata key " + key + " already exist");
        }

        nodeMetadata.get(node).put(key, value);

        if (nodesByMetadataValue.containsKey(value) && nodesByMetadataValue.get(value) != node) {
            throw new IllegalArgumentException("node "+node+": metadata value " + value + " is already associated with node "+ nodesByMetadataValue.get(value));
        }
        nodesByMetadataValue.put(value, node);
    }

    @Override
    public String getNodeMetadataValue(int node, String key) {

        if (!nodeMetadata.containsKey(node)) {
            return null;
        }

        if (!nodeMetadata.get(node).containsKey(key)) {
            return null;
        }

        return nodeMetadata.get(node).get(key);
    }

    @Override
    public void addNode(int node) {

        assert !nodes.contains(node) : node;

        if (node < 0) {
            throw new IllegalStateException("node cannot be negative");
        }

        nodes.add(node);
    }

    @Override
    public int getNodeFromMetadata(String value) {

        if (!nodesByMetadataValue.containsKey(value)) {
            return -1;
        }

        return nodesByMetadataValue.get(value);
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

    @Override
    public void setEdgeLabel(int edge, String label) {

        if (!containsEdge(edge)) {
            throw new IllegalArgumentException("edge " + edge+" does not exist");
        }

        edgeLabels.put(edge, label);
    }

    @Override
    public String getEdgeLabel(int edge) {

        return edgeLabels.get(edge);
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
    public int getEdge(int tail, int head) {

        for (int i=0 ; i<tails.size() ; i++) {

            if (tails.get(i) == tail && heads.get(i) == head) {
                 return i;
            }
        }
        return -1;
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

    private void findMatchingPointEdges(int node, TIntList endPoints, TIntSet edges) {

        for (int i=0 ; i<endPoints.size() ; i++) {

            if (endPoints.get(i) == node) {
                edges.add(i);
            }
        }
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

    @Override
    public int[] getDescendants(int node) {

        TIntSet descendants = new TIntHashSet();

        getDescendants(node, descendants);

        return descendants.toArray();
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

    private void getDescendants(int node, TIntSet descendants) {

        if (!successorLists.containsKey(node)) {
            return;
        }

        successorLists.get(node).forEach(successor -> {

            descendants.add(successor);
            getDescendants(successor, descendants);

            return true;
        });
    }

    @Override
    public boolean isAncestorOf(int queryAncestor, int queryDescendant) {

        return arrayContainsElement(getAncestors(queryDescendant), queryAncestor);
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
    public IntGraph calcSubgraph(int... nodes) {

        IntGraph sg = new IntGraph(graphLabel + " (subgraph)");

        for (int node : nodes) {
             sg.addNode(node);
             if (nodeMetadata.containsKey(node)) {
                 sg.nodeMetadata.put(node, new HashMap<>(nodeMetadata.get(node)));
             }
        }

        // get all incident edges
        int[] edges = getEdgesIncidentTo(sg.getNodes());

        // then add only the ones where heads and tails are contained in nodes
        for (int i=0; i < edges.length ; i++) {

            int tail = getTailNode(edges[i]);
            int head = getHeadNode(edges[i]);

            if (sg.containsNode(tail) && sg.containsNode(head)) {
                sg.addEdge(tail, head);
            }
        }

        return sg;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeObject(graphLabel);
        ((TIntArrayList)nodes).writeExternal(out);
        ((TIntArrayList)tails).writeExternal(out);
        ((TIntArrayList)heads).writeExternal(out);
        ((TIntObjectHashMap)predecessorLists).writeExternal(out);
        ((TIntObjectHashMap)successorLists).writeExternal(out);
        ((TIntObjectHashMap)nodeMetadata).writeExternal(out);
        ((TObjectIntHashMap)nodesByMetadataValue).writeExternal(out);
        ((TIntObjectHashMap)edgeLabels).writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        graphLabel = (String) in.readObject();
        ((TIntArrayList)nodes).readExternal(in);
        ((TIntArrayList)tails).readExternal(in);
        ((TIntArrayList)heads).readExternal(in);
        ((TIntObjectHashMap)predecessorLists).readExternal(in);
        ((TIntObjectHashMap)successorLists).readExternal(in);
        ((TIntObjectHashMap)nodeMetadata).readExternal(in);
        ((TObjectIntHashMap)nodesByMetadataValue).readExternal(in);
        ((TIntObjectHashMap)edgeLabels).readExternal(in);
    }

    static boolean arrayContainsElement(int[] array, int element) {

        for (int anArray : array) {
            if (anArray == element) {
                return true;
            }
        }

        return false;
    }
}
