package org.nextprot.api.commons.graph;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class IntGraphTest extends BaseIntGraphTest {

    @Override
    protected DirectedGraph createGraph() {

        return new IntGraph();
    }

    @Test
    public void testConstr() {

        IntGraph g = new IntGraph();
        Assert.assertEquals("", g.getGraphLabel());
    }

    @Test(expected = NullPointerException.class)
    public void testConstrUndefinedLabel() {

        new IntGraph(null);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {

        IntGraph graph = (IntGraph) createGraph();
        populateExampleGraph(graph);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(graph);
        oos.flush();
        oos.close();

        DirectedGraph graphRead;
        InputStream sis = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(sis);
        graphRead = (IntGraph)ois.readObject();
        ois.close();

        Assert.assertEquals("", graphRead.getGraphLabel());
        Assert.assertEquals(7, graphRead.countNodes());
        Assert.assertEquals(1, graphRead.getNodes()[0]);
        Assert.assertEquals(8, graphRead.countEdges());
        Assert.assertEquals(1, graphRead.getTailNode(graphRead.getEdges()[0]));
        Assert.assertEquals(2, graphRead.getHeadNode(graphRead.getEdges()[0]));
        Assert.assertEquals(2, graphRead.getTailNode(graphRead.getEdges()[1]));
        Assert.assertEquals(3, graphRead.getHeadNode(graphRead.getEdges()[1]));
    }
}