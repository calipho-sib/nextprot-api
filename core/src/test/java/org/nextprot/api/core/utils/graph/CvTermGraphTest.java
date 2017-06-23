package org.nextprot.api.core.utils.graph;

import org.junit.Assert;
import org.junit.Test;

public class CvTermGraphTest {

    @Test
    public void addNode() throws Exception {

        DirectedGraph graph = new CvTermGraph();

        graph.addNode(0);
        graph.addNode(1);

        Assert.assertTrue(graph.containsNode(0));
        Assert.assertTrue(graph.containsNode(1));
        Assert.assertTrue(!graph.containsNode(2));
        Assert.assertEquals(2, graph.countNodes());
    }

    @Test
    public void addSameNodes() throws Exception {

        DirectedGraph graph = new CvTermGraph();

        graph.addNode(0);
        graph.addNode(0);

        Assert.assertEquals(1, graph.countNodes());
    }

    @Test
    public void addEdge() throws Exception {

        DirectedGraph graph = new CvTermGraph();

        int edgeId = graph.addEdge(0, 1);

        Assert.assertTrue(graph.containsEdge(edgeId));
        Assert.assertTrue(!graph.containsEdge(23));
        Assert.assertEquals(1, graph.countEdges());
        Assert.assertEquals(0, edgeId);
        Assert.assertEquals(0, graph.getTailNode(edgeId));
        Assert.assertEquals(1, graph.getHeadNode(edgeId));
    }

    @Test
    public void getNodes() throws Exception {
    }

    @Test
    public void getEdges() throws Exception {
    }

    @Test
    public void getEdgeNodes() throws Exception {
    }

    @Test
    public void containsNode() throws Exception {
    }

    @Test
    public void containsEdge() throws Exception {
    }

    @Test
    public void getAncestors() throws Exception {
    }

    @Test
    public void isAncestorOf() throws Exception {
    }

    @Test
    public void calcAllPaths() throws Exception {
    }

}