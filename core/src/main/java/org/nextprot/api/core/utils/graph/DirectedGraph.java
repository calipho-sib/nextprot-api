package org.nextprot.api.core.utils.graph;

import grph.path.Path;

import java.util.Collection;

/**
 * A graph with a set of vertices connected by edges, where the edges have a direction associated with them.
 * All nodes and edges are ints.
 *
 * Created by fnikitin on 23.06.17.
 */
public interface DirectedGraph {

    /**
     * Add a node in graph
     * @param node
     */
    void addNode(int node);

    /**
     * Add an edge in graph from tail to head
     * @param tail predecessor node
     * @param head successor node
     * @return the edge id or -1 if already exists
     */
    int addEdge(int tail, int head);

    /**
     * @return an array of graph nodes
     */
    int[] getNodes();

    /**
     * @return an array of graph edges
     */
    int[] getEdges();

    int[] getEdgesIncidentTo(int... nodes);
    int[] getInEdges(int... nodes);
    int[] getOutEdges(int... nodes);

    /**
     * @return the tail node of the given edge
     */
    int getTailNode(int edge);

    /**
     * @return the head node of the given edge
     */
    int getHeadNode(int edge);

    /**
     * @return true if node belongs to graph else false
     */
    boolean containsNode(int node);

    /**
     * @return true if edge belongs to graph else false
     */
    boolean containsEdge(int edge);

    /**
     * @return true if tail -> head edge belongs to graph else false
     */
    boolean containsEdge(int tail, int head);

    /**
     * @return ancestors of the given node
     */
    int[] getAncestors(int node);

    /**
     * @return true if queryDescendant is a descendant of queryAncestor
     */
    boolean isAncestorOf(int queryAncestor, int queryDescendant);

    /**
     * @return the predecessors of the given node
     */
    int[] getPredecessors(int node);

    /**
     * @return the successors of the given node
     */
    int[] getSuccessors(int node);

    /**
     * @return the number of head ends adjacent to the given node
     */
    int getInDegree(int node);

    /**
     * @return the number of tail ends adjacent to the given node
     */
    int getOutDegree(int node);

    Collection<Path> calcAllPaths();

    DirectedGraph calcAncestorSubgraph(int node);

    /**
     * @return the total number of graph nodes
     */
    default int countNodes() {

        return getNodes().length;
    }

    /**
     * @return the total number of graph edges
     */
    default int countEdges() {

        return getEdges().length;
    }

    /**
     * @return true if queryDescendant is a descendant of queryAncestor
     */
    default boolean isDescendantOf(int queryDescendant, int queryAncestor) {

        return isAncestorOf(queryAncestor, queryDescendant);
    }

    default boolean isSource(int node) {
        return getInDegree(node) == 0 && getOutDegree(node) > 0;
    }

    default boolean isSink(int node) {
        return getInDegree(node) > 0 && getOutDegree(node) == 0;
    }

    default boolean isInternal(int node) {
        return !isSource(node) && !isSink(node);
    }
}
