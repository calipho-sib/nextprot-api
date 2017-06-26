package org.nextprot.api.core.utils.graph;

import grph.path.Path;

import java.util.Collection;

/**
 * Created by fnikitin on 23.06.17.
 */
public interface DirectedGraph {

    void addNode(int node);

    /**
     * Add edge
     * @param tail
     * @param head
     * @return the edge id or -1 if already exists
     */
    int addEdge(int tail, int head);

    int[] getNodes();

    int[] getEdges();

    int getTailNode(int edge);

    int getHeadNode(int edge);

    boolean containsNode(int node);

    boolean containsEdge(int edge);

    int[] getAncestors(int cvTermId);

    boolean isAncestorOf(int queryAncestor, int queryDescendant);

    default int countNodes() {

        return getNodes().length;
    }

    default int countEdges() {

        return getEdges().length;
    }

    default boolean isChildOf(int queryDescendant, int queryAncestor) {

        return isAncestorOf(queryAncestor, queryDescendant);
    }
    
    Collection<Path> calcAllPaths();

    DirectedGraph calcAncestorSubgraph(int node);
}
