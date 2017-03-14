package org.nextprot.api.core.utils.graph;

import grph.path.Path;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ActiveProfiles({"dev"})
public class OntologyDAGTest extends CoreUnitBaseTest {

    @Autowired
    private TerminologyService terminologyService;

    @Test
    public void shouldCreateValidGeneOntologyGraph() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        Assert.assertEquals(TerminologyCv.GoMolecularFunctionCv, graph.getTerminologyCv());
        Assert.assertEquals(10543, graph.countNodes());
        Assert.assertEquals(12797, graph.countEdges());
    }

    @Test
    public void nodeGO0005488ShouldHaveChildren() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(50, graph.getChildren(cvId).count());
        Assert.assertEquals(graph.getCvTermById(cvId).getChildAccession().size(), graph.getChildren(cvId).count());
        Assert.assertTrue(graph.getChildren(cvId)
                .map(graph::getCvTermById)
                .map(CvTerm::getAccession).collect(Collectors.toSet()).contains("GO:0030246"));
        Assert.assertTrue(graph.getChildren(cvId)
                .map(graph::getCvTermById)
                .map(CvTerm::getAccession).collect(Collectors.toSet()).contains("GO:0001871"));
    }

    @Test
    public void nodeGO0005488ShouldHaveOneParent() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(1, graph.getParents(cvId).count());
        Assert.assertTrue(graph.getParents(cvId)
                .map(graph::getCvTermById)
                .map(CvTerm::getAccession).collect(Collectors.toSet()).contains("GO:0003674"));
    }

    @Test
    public void nodeGO0000006ShouldBeALeaf() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long cvId = graph.getCvTermIdByAccession("GO:0000006");

        Assert.assertEquals(0, graph.getChildren(cvId).count());
    }

    @Test
    public void geneOntologyShouldContainOneRoot() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        Stream<Long> roots = graph.getRoots();
        Assert.assertEquals(1, roots.count());
        CvTerm root = graph.getRoots()
                .map(graph::getCvTermById)
                .collect(Collectors.toList())
                .get(0);
        Assert.assertEquals("GO:0003674", root.getAccession());
    }

    @Test
    public void GO0005488shouldBeAncestorOfGO0005488() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        Assert.assertTrue(graph.isAncestorOf("GO:0005488", "GO:0051378"));
        Assert.assertTrue(graph.isAncestorOfSlow("GO:0005488", "GO:0051378"));
    }

    @Test
    public void roudoudouShouldNotExistInOntology() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        Assert.assertTrue(graph.hasCvTermAccession("roudoudou"));
    }

    @Ignore
    @Test
    public void benchmarkingIsAncestorMethods() throws Exception {

        benchmarkingIsAncestorMethods(TerminologyCv.MeshCv, true);
    }

    private void benchmarkingIsAncestorMethods(TerminologyCv terminologyCv, boolean both) {

        System.err.println("Timing isAncestorOf() for all paths of "+terminologyCv+" graph:");
        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(terminologyCv.name());
        OntologyDAG graph = new OntologyDAG(terminologyCv, cvTerms);

        Collection<Path> allPaths = graph.getAllPaths();

        long precomputationExecTime = 0;
        long normalExecTime = 0;

        Instant t1 = Instant.now();

        for (Path path : allPaths) {

            graph.isAncestorOf(path.getSource(), path.getDestination());
        }
        precomputationExecTime = ChronoUnit.MILLIS.between(t1, Instant.now());
        System.err.println("with precomputations: "+precomputationExecTime+" ms");

        if (both) {
            t1 = Instant.now();

            for (Path path : allPaths) {

                graph.isAncestorOfSlow(path.getSource(), path.getDestination());
            }
            normalExecTime = ChronoUnit.MILLIS.between(t1, Instant.now());

            System.err.println("without precomputations: "+normalExecTime+ " ms");
            // 1232024 ms (20')
        }

        if (both) {

            System.err.println("precomputation speed up: x" + (normalExecTime / precomputationExecTime));
            // x18954
        }
    }
}