package org.nextprot.api.commons.utils.graph;

public class IntGrphTest extends BaseIntGraphTest {

    @Override
    protected DirectedGraph createGraph(String title) {

        DirectedGraph graph = new IntGrph();
        graph.setGraphLabel(title);
        return graph;
    }
}