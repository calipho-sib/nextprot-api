package org.nextprot.api.core.utils.graph;

import gnu.trove.set.hash.TIntHashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class BaseIntGraphTest {

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

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(5, 2);
        graph.addEdge(5, 4);
        graph.addEdge(4, 3);
        graph.addEdge(7, 4);
        graph.addEdge(6, 5);
        graph.addEdge(6, 7);

        Assert.assertEquals(2, graph.getInDegree(4));
        Assert.assertEquals(1, graph.getOutDegree(4));
        Assert.assertEquals(2, graph.getOutDegree(5));
    }

    @Test
    public void getAncestors() throws Exception {

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(5, 2);
        graph.addEdge(5, 4);
        graph.addEdge(4, 3);
        graph.addEdge(7, 4);
        graph.addEdge(6, 5);
        graph.addEdge(6, 7);

        Assert.assertTrue(new TIntHashSet(graph.getAncestors(4))
                .containsAll(new int[] {5, 6, 7}));

        Assert.assertTrue(graph.isAncestorOf(6, 3));
        Assert.assertTrue(!graph.isAncestorOf(3, 6));
        Assert.assertTrue(graph.isDescendantOf(3, 6));
    }

    @Test
    public void getIncidentEdges() throws Exception {

        graph.addEdge(1, 2); // *
        graph.addEdge(2, 3); // *
        graph.addEdge(5, 2); // *
        graph.addEdge(5, 4);
        graph.addEdge(4, 3);
        graph.addEdge(7, 4);
        graph.addEdge(6, 5);
        graph.addEdge(6, 7);

        Assert.assertTrue(new TIntHashSet(graph.getEdgesIncidentTo(2))
                .containsAll(new int[] {0, 1, 2}));
    }

    @Test
    public void getAncestorSubgraph() throws Exception {

        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(5, 2);
        graph.addEdge(5, 4);
        graph.addEdge(4, 3);
        graph.addEdge(7, 4);
        graph.addEdge(6, 5);
        graph.addEdge(6, 7);

        DirectedGraph sg = graph.calcAncestorSubgraph(4);

        Assert.assertTrue(new TIntHashSet(sg.getNodes()).containsAll(new int[] {4, 6, 7}));
    }
}