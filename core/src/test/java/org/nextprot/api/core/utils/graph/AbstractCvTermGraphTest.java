package org.nextprot.api.core.utils.graph;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveProfiles({"dev","cache"})
public abstract class AbstractCvTermGraphTest extends CoreUnitBaseTest {

    @Autowired
    private TerminologyService terminologyService;

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

        Assert.assertTrue(graph.containsEdge(edgeId));
        Assert.assertTrue(!graph.containsEdge(23));
        Assert.assertEquals(1, graph.countEdges());
        Assert.assertEquals(0, edgeId);
        Assert.assertEquals(0, graph.getTailNode(edgeId));
        Assert.assertEquals(1, graph.getHeadNode(edgeId));
    }

    @Test
    public void shouldCreateValidGeneOntologyGraph() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());

        final Map<String, Integer> cvTermIdByAccession = new HashMap<>(cvTerms.size());

        cvTerms.forEach(cvt -> {
            cvTermIdByAccession.put(cvt.getAccession(), Math.toIntExact(cvt.getId()));
            graph.addNode(Math.toIntExact(cvt.getId()));
        });

        cvTerms.forEach(cvt -> {
            List<String> parentAccessions = cvt.getAncestorAccession();

            if (parentAccessions != null) {
                parentAccessions.forEach(parent -> {
                    try {
                        graph.addEdge(cvTermIdByAccession.get(parent), Math.toIntExact(cvt.getId()));
                    } catch (IllegalStateException e) {
                        System.err.println(" cannot connect to unknown node parent: "+e.getMessage());
                    }
                });
            }
        });

        Assert.assertEquals(10543, graph.countNodes());
        Assert.assertEquals(12797, graph.countEdges());
    }

    @Test
    public void isAncestorOf() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());

        final Map<String, Integer> cvTermIdByAccession = new HashMap<>(cvTerms.size());

        cvTerms.forEach(cvt -> {
            cvTermIdByAccession.put(cvt.getAccession(), Math.toIntExact(cvt.getId()));
            graph.addNode(Math.toIntExact(cvt.getId()));
        });

        cvTerms.forEach(cvt -> {
            List<String> parentAccessions = cvt.getAncestorAccession();

            if (parentAccessions != null) {
                parentAccessions.forEach(parent -> {
                    try {
                        graph.addEdge(cvTermIdByAccession.get(parent), Math.toIntExact(cvt.getId()));
                    } catch (IllegalStateException e) {
                        System.err.println(" cannot connect to unknown node parent: "+e.getMessage());
                    }
                });
            }
        });

        int ancestorId = cvTermIdByAccession.get("GO:0005488");
        int descendantId = cvTermIdByAccession.get("GO:0051378");

        Assert.assertTrue(graph.isAncestorOf(ancestorId, descendantId));
        Assert.assertTrue(graph.isChildOf(descendantId, ancestorId));
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
    public void getAncestors() throws Exception {
    }



    @Test
    public void calcAllPaths() throws Exception {
    }

}