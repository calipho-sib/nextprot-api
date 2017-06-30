package org.nextprot.api.commons.utils.graph;

/**
 * A graph with a set of vertices connected by edges, where the edges have a direction associated with them.
 * All nodes and edges are ints.
 *
 * Created by fnikitin on 23.06.17.
 */
public interface DirectedGraph {

    /**
     * Set a graph label
     * @param label a graph label
     */
    void setGraphLabel(String label);

    /**
     * @return the graph label
     */
    String getGraphLabel();

    /**
     * Add a node in graph
     * @param node
     * @throws IllegalArgumentException if node is negative
     */
    void addNode(int node);

    /**
     * Add a node with its label in graph
     * @param node
     * @param label
     * @throws IllegalArgumentException if node is negative
     */
    void addNode(int node, String label);

    /**
     * Set a node label
     * @param node a node id
     * @param label a node label
     */
    void setNodeLabel(int node, String label);

    /**
     * @return the node label or null if not defined
     */
    String getNodeLabel(int node);

    /**
     * @return the node given the label or -1 if not found
     */
    int getNode(String label);

    /**
     * Add an edge in graph from tail to head
     * @param tail predecessor node
     * @param head successor node
     * @return the edge id or -1 if already exists
     */
    int addEdge(int tail, int head);

    /**
     * Set an edge label
     * @param edge a edge id
     * @param label a node label
     */
    void setEdgeLabel(int edge, String label);

    /**
     * @return the edge label
     */
    String getEdgeLabel(int edge);

    /**
     * @return an array of graph nodes
     */
    int[] getNodes();

    /**
     * @return an array of graph edges
     */
    int[] getEdges();

    /**
     * @return the edge id from end points or -1 if not found
     */
    int getEdge(int tail, int head);

    int[] getEdgesIncidentTo(int... nodes);
    int[] getInEdges(int... nodes);
    int[] getOutEdges(int... nodes);

    /**
     * @return the tail node of the given edge or -1 if not found
     */
    int getTailNode(int edge);

    /**
     * @return the head node of the given edge or -1 if not found
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

    /**
     * @return the sources (indegree = 0) of the graph
     */
    int[] getSources();

    /**
     * @return the sinks (outdegree = 0) of the graph
     */
    int[] getSinks();

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
}
