package org.nextprot.api.core.utils.graph;

import grph.path.Path;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
        Assert.assertEquals(12797, graph.countEdgesFromTransientGraph());
        Assert.assertTrue(graph.isTransientGraphAvailable());
    }

    @Test
    public void nodeGO0005488ShouldHaveChildren() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(50, graph.getChildrenFromGrph(cvId).length);

        CvTerm cvTerm = terminologyService.findCvTermByAccession(graph.getCvTermAccessionById(cvId));

        Assert.assertEquals(cvTerm.getChildAccession().size(), graph.getChildrenFromGrph(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getChildrenFromGrph(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0030246"));
        Assert.assertTrue(Arrays.stream(graph.getChildrenFromGrph(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0001871"));
    }

    @Test
    public void nodeGO0005488ShouldHaveOneParent() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(1, graph.getParentsFromGrph(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getParentsFromGrph(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0003674"));
    }

    @Test
    public void nodeGO0000006ShouldBeALeaf() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long cvId = graph.getCvTermIdByAccession("GO:0000006");

        Assert.assertEquals(0, graph.getChildrenFromGrph(cvId).length);
    }

    @Test
    public void geneOntologyShouldContainOneRoot() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        Stream<Long> roots = graph.getRoots();
        Assert.assertEquals(1, roots.count());
        String rootAccession = graph.getRoots()
                .map(graph::getCvTermAccessionById)
                .collect(Collectors.toList())
                .get(0);
        Assert.assertEquals("GO:0003674", rootAccession);
    }

    @Test
    public void GO0005488shouldBeAncestorOfGO0005488() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        long ancestorId = graph.getCvTermIdByAccession("GO:0005488");
        long descendantId = graph.getCvTermIdByAccession("GO:0051378");

        Assert.assertTrue(graph.isAncestorOf(ancestorId, descendantId));
        Assert.assertTrue(graph.isAncestorOfSlow(ancestorId, descendantId));
        Assert.assertTrue(graph.isChildOf(descendantId, ancestorId));
    }

    @Test
    public void roudoudouShouldNotExistInOntology() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        Assert.assertFalse(graph.hasCvTermAccession("roudoudou"));
    }

    @Test
    public void testGetAncestorFromTerminologyUtils() throws Exception {

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name());
        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, cvTerms);

        String cvtermAccession = "GO:1904047";

        Set<Long> expectedAncestors = TerminologyUtils.getAllAncestors(cvtermAccession, terminologyService).stream()
                .map(accession -> {
            try {
                return graph.getCvTermIdByAccession(accession);
            } catch (OntologyDAG.NotFoundNodeException e) {
                return -1L;
            }
        }).collect(Collectors.toSet());

        Set<Long> observedAncestors = Arrays.stream(graph.getAncestors(graph.getCvTermIdByAccession(cvtermAccession))).boxed().collect(Collectors.toSet());

        Assert.assertEquals(expectedAncestors, observedAncestors);
    }

    @Test
    public void benchmarkingGetAncestorsMethods() throws Exception {

        benchmarkingGetAncestorsMethods(TerminologyCv.MeshCv);
        benchmarkingGetAncestorsMethods(TerminologyCv.MeshCv);
        benchmarkingGetAncestorsMethods(TerminologyCv.MeshCv);

        /*for (TerminologyCv ontology : TerminologyCv.values()) {

            benchmarkingGetAncestorsMethods(ontology);
        }*/
    }

    @Ignore
    @Test
    public void benchmarkingIsAncestorMethods() throws Exception {

        benchmarkingIsAncestorMethods(TerminologyCv.MeshCv, true);
    }

    private void benchmarkingIsAncestorMethods(TerminologyCv terminologyCv, boolean both) throws OntologyDAG.NotFoundInternalGrphException {

        System.err.println("Timing isAncestorOf() for all paths of "+terminologyCv+" graph:");
        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(terminologyCv.name());
        OntologyDAG graph = new OntologyDAG(terminologyCv, cvTerms);

        Collection<Path> allPaths = graph.getAllPathsFromTransientGraph();

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

    private void benchmarkingGetAncestorsMethods(TerminologyCv terminologyCv) {

        System.out.println("benchmarking "+terminologyCv+"...");

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(terminologyCv.name());
        OntologyDAG graph = new OntologyDAG(terminologyCv, cvTerms);

        Map<Long, List<String>> ancestors = new HashMap<>();
        Map<Long, long[]> ancestorsQuick = new HashMap<>();

        Instant t = Instant.now();
        for (CvTerm cvTerm : cvTerms) {

            ancestorsQuick.put(cvTerm.getId(), graph.getAncestors(cvTerm.getId()));
        }
        System.out.println("OntologyDAG.getAncestors(): "+ChronoUnit.MILLIS.between(t, Instant.now()) + " ms");

        t = Instant.now();
        for (CvTerm cvTerm : cvTerms) {

            ancestors.put(cvTerm.getId(), TerminologyUtils.getAllAncestors(cvTerm.getAccession(), terminologyService));
        }
        System.out.println("TerminologyUtils.getAllAncestors(): "+ ChronoUnit.SECONDS.between(t, Instant.now()) + " ms");

        checkAncestors(ancestors, ancestorsQuick, graph);
    }

    private void checkAncestors(Map<Long, List<String>> ancestors, Map<Long, long[]> ancestorsQuick, OntologyDAG graph) {

        Assert.assertEquals(ancestors.size(), ancestorsQuick.size());
        Set<Long> ids = ancestors.keySet();

        for (long id : ids) {

            Set<Long> ancestorsOld = ancestors.get(id).stream().map(accession -> {
                try {
                    return graph.getCvTermIdByAccession(accession);
                } catch (OntologyDAG.NotFoundNodeException e) {
                    return -1L;
                }
            }).collect(Collectors.toSet());

            Set<Long> ancestorsNew = Arrays.stream(ancestorsQuick.get(id)).boxed().collect(Collectors.toSet());

            Assert.assertEquals(ancestorsOld, ancestorsNew);
        }
    }
}