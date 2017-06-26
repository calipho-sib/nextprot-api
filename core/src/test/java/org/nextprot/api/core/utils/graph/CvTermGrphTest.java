package org.nextprot.api.core.utils.graph;

public class CvTermGrphTest extends AbstractCvTermGraphTest {

    @Override
    protected DirectedGraph createGraph() {

        return new CvTermGrph();
    }
}