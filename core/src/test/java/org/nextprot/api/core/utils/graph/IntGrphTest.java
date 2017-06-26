package org.nextprot.api.core.utils.graph;

public class IntGrphTest extends AbstractIntGraphTest {

    @Override
    protected DirectedGraph createGraph() {

        return new IntGrph();
    }
}