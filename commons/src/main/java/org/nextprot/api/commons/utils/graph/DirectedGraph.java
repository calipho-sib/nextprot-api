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
     * Associating metadata to node
     * @param node the node id
     * @param key the metadata key
     * @param value the metadata value
     */
    void addNodeMetadata(int node, String key, String value);

    /**
     * Get the value of the metadata associated with the given node
     * @param node the node to get metadata for
     * @param key the metadata key
     * @return the value or null if not found
     */
    String getNodeMetadataValue(int node, String key);

    /**
     * @return the node given the label or -1 if not found
     */
    int getNodeFromMetadata(String metadata);

    /**
     * Add an edge in graph from tail to head and return its index
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

    /**
     * Get the incoming and outcoming edges for the given nodes
     * @param nodes the nodes
     * @return edge indices
     */
    int[] getEdgesIncidentTo(int... nodes);

    /**
     * Get the incoming edges to the given nodes
     * @param nodes the nodes
     * @return edge indices
     */
    int[] getInEdges(int... nodes);

    /**
     * Get the outcoming edges from the given nodes
     * @param nodes the nodes
     * @return edge indices
     */
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
     * @return descendants of the given node
     */
    int[] getDescendants(int node);

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

    /**
     * @return the subgraph of this graph composed of given nodes
     */
    DirectedGraph calcSubgraph(int... nodes);

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
