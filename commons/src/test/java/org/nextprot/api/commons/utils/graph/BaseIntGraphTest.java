package org.nextprot.api.commons.utils.graph;

import gnu.trove.set.hash.TIntHashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import toools.collections.Arrays;

abstract class BaseIntGraphTest {

    private DirectedGraph graph;
    protected abstract DirectedGraph createGraph();

    @Before
    public void setup() throws Exception {
        graph = createGraph();
    }

    @Test
    public void addNode() throws Exception {

        graph.addNode(0);
        graph.addNode(1);

        Assert.assertTrue(graph.containsNode(0));
        Assert.assertTrue(graph.containsNode(1));
        Assert.assertTrue(!graph.containsNode(2));
        Assert.assertEquals(2, graph.countNodes());
    }

    @Test(expected = java.lang.AssertionError.class)
    public void addSameNodes() throws Exception {

        graph.addNode(0);
        graph.addNode(0);

        Assert.assertEquals(1, graph.countNodes());
    }

    @Test
    public void addEdge() throws Exception {

        int edgeId = graph.addEdge(0, 1);

        Assert.assertTrue(graph.containsNode(0));
        Assert.assertTrue(graph.containsNode(1));
        Assert.assertTrue(graph.containsEdge(edgeId));
        Assert.assertTrue(!graph.containsEdge(23));
        Assert.assertEquals(1, graph.countEdges());
        Assert.assertEquals(0, edgeId);
        Assert.assertEquals(0, graph.getTailNode(edgeId));
        Assert.assertEquals(1, graph.getHeadNode(edgeId));
    }

    @Test
    public void getNodes() throws Exception {

        graph.addNode(0);
        graph.addNode(1);

        Assert.assertArrayEquals(new int[] {0, 1}, graph.getNodes());
    }

    @Test
    public void getEdges() throws Exception {

        graph.addEdge(5, 2);
        graph.addEdge(5, 4);

        Assert.assertArrayEquals(new int[] {0, 1}, graph.getEdges());
    }

    @Test
    public void getEdgeNodes() throws Exception {

        graph.addEdge(5, 2);
        graph.addEdge(5, 4);

        Assert.assertEquals(5, graph.getTailNode(0));
        Assert.assertEquals(2, graph.getHeadNode(0));
        Assert.assertEquals(5, graph.getTailNode(1));
        Assert.assertEquals(4, graph.getHeadNode(1));
    }

    @Test
    public void testGetDegree() throws Exception {

        populateExampleGraph(graph);

        Assert.assertEquals(2, graph.getInDegree(4));
        Assert.assertEquals(1, graph.getOutDegree(4));
        Assert.assertEquals(2, graph.getOutDegree(5));
    }

    @Test
    public void getAncestors() throws Exception {

        populateExampleGraph(graph);

        Assert.assertTrue(new TIntHashSet(graph.getAncestors(4))
                .containsAll(new int[] {5, 6, 7}));

        Assert.assertTrue(graph.isAncestorOf(6, 3));
        Assert.assertTrue(!graph.isAncestorOf(3, 6));
        Assert.assertTrue(graph.isDescendantOf(3, 6));
    }

    @Test
    public void getIncidentEdges() throws Exception {

        populateExampleGraph(graph);

        Assert.assertTrue(new TIntHashSet(graph.getEdgesIncidentTo(2))
                .containsAll(new int[] {0, 1, 2}));
    }

    @Test
    public void getInEdges() throws Exception {

        populateExampleGraph(graph);

        Assert.assertTrue(Arrays.contains(graph.getInEdges(2), 0));
        Assert.assertTrue(Arrays.contains(graph.getInEdges(2), 2));
    }

    @Test
    public void getOutEdges() throws Exception {

        populateExampleGraph(graph);

        Assert.assertTrue(Arrays.contains(graph.getOutEdges(5), 2));
        Assert.assertTrue(Arrays.contains(graph.getOutEdges(5), 3));
    }

    @Test
    public void getAncestorSubgraph() throws Exception {

        populateExampleGraph(graph);

        DirectedGraph sg = graph.calcSubgraph(4, 6, 7, 5);

        Assert.assertEquals(4, sg.countNodes());
        Assert.assertTrue(new TIntHashSet(sg.getNodes()).containsAll(new int[] {4, 6, 7, 5}));
        //Assert.assertEquals(4, sg.countEdges());
        Assert.assertTrue(sg.containsEdge(6, 5));
        Assert.assertTrue(sg.containsEdge(6, 7));
        Assert.assertTrue(sg.containsEdge(5, 4));
        Assert.assertTrue(sg.containsEdge(7, 4));
    }

    @Test
    public void getEdgeByEndPoints() throws Exception {

        populateExampleGraph(graph);

        Assert.assertEquals(6, graph.getEdge(6, 5));
    }

    @Test
    public void couldNotGetEdgeByEndPoints() throws Exception {

        populateExampleGraph(graph);

        Assert.assertEquals(-1, graph.getEdge(5, 6));
    }

    @Test
    public void couldNotGetEdgeByMissingEndPoint() throws Exception {

        populateExampleGraph(graph);

        Assert.assertEquals(-1, graph.getEdge(16, 5));
    }

    @Test
    public void setGraphlabel() throws Exception {

        graph.setGraphLabel("maurice");

        Assert.assertEquals("maurice", graph.getGraphLabel());
    }

    @Test
    public void setNodelabel() throws Exception {

        populateExampleGraph(graph);
        graph.addNodeMetadata(1, "label", "node 1");

        Assert.assertEquals("node 1", graph.getNodeMetadataValue(1, "label"));
        Assert.assertEquals(null, graph.getNodeMetadataValue(2, "label"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setNodelabelUnknownNode() throws Exception {

        populateExampleGraph(graph);
        graph.addNodeMetadata(14,"label","node 1");
    }

    @Test
    public void setEdgelabel() throws Exception {

        populateExampleGraph(graph);
        graph.setEdgeLabel(1, "edge 1");

        Assert.assertEquals("edge 1", graph.getEdgeLabel(1));
        Assert.assertEquals(null, graph.getEdgeLabel(2));
    }

    /*
                1 ---2--3
            6__ 5 __/  /
              \     \ /
               \     4
                7 __/
    */
    static void populateExampleGraph(DirectedGraph graph) {

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(5, 2);
        graph.addEdge(5, 4);
        graph.addEdge(4, 3);
        graph.addEdge(7, 4);
        graph.addEdge(6, 5);
        graph.addEdge(6, 7);
    }
}