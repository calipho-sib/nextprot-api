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

@ActiveProfiles({"dev","cache"})
public class OntologyDAGTest extends CoreUnitBaseTest {

    @Autowired
    private TerminologyService terminologyService;

    @Test
    public void shouldCreateValidGeneOntologyGraph() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertEquals(TerminologyCv.GoMolecularFunctionCv, graph.getTerminologyCv());
        Assert.assertEquals(10543, graph.countNodes());
        Assert.assertEquals(12797, graph.countEdgesFromTransientGraph());
        Assert.assertTrue(graph.isTransientGraphAvailable());
    }

    @Test
    public void nodeGO0005488ShouldHaveChildren() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        long cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(50, graph.getChildrenFromTransientGraph(cvId).length);

        CvTerm cvTerm = terminologyService.findCvTermByAccession(graph.getCvTermAccessionById(cvId));

        Assert.assertEquals(cvTerm.getChildAccession().size(), graph.getChildrenFromTransientGraph(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getChildrenFromTransientGraph(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0030246"));
        Assert.assertTrue(Arrays.stream(graph.getChildrenFromTransientGraph(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0001871"));
    }

    @Test
    public void nodeGO0005488ShouldHaveOneParent() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        long cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(1, graph.getParentsFromTransientGraph(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getParentsFromTransientGraph(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0003674"));
    }

    @Test
    public void nodeGO0000006ShouldBeALeaf() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        long cvId = graph.getCvTermIdByAccession("GO:0000006");

        Assert.assertEquals(0, graph.getChildrenFromTransientGraph(cvId).length);
    }

    @Test
    public void geneOntologyShouldContainOneRoot() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

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

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        long ancestorId = graph.getCvTermIdByAccession("GO:0005488");
        long descendantId = graph.getCvTermIdByAccession("GO:0051378");

        Assert.assertTrue(graph.isAncestorOf(ancestorId, descendantId));
        Assert.assertTrue(graph.isAncestorOfSlow(ancestorId, descendantId));
        Assert.assertTrue(graph.isChildOf(descendantId, ancestorId));
    }

    @Test
    public void roudoudouShouldNotExistInOntology() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertFalse(graph.hasCvTermAccession("roudoudou"));
    }

    @Test
    public void benchmarkingGetAncestorsMethods() throws Exception {

        Set<TerminologyCv> excludedOntology = EnumSet.of(
                TerminologyCv.NextprotCellosaurusCv, TerminologyCv.MeshAnatomyCv, TerminologyCv.MeshCv);

        Set<String> allCvTerms = new HashSet<>();

        // cache all (211367 terms)
        Instant t = Instant.now();
        for (TerminologyCv ontology : TerminologyCv.values()) {

            allCvTerms.addAll(terminologyService.findCvTermsByOntology(ontology.name()).stream()
                    .map(CvTerm::getAccession)
                    .collect(Collectors.toSet()));
        }
        System.out.println("access/cache "+TerminologyCv.values().length+" terminologies via terminologyService.findCvTermsByOntology: "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");

        t = Instant.now();
        allCvTerms.forEach(cvTerm -> terminologyService.findCvTermByAccession(cvTerm));
        System.out.println("access/cache "+allCvTerms.size()+" terms via terminologyService.findCvTermByAccession: "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");

        for (TerminologyCv ontology : TerminologyCv.values()) {

            if (excludedOntology.contains(ontology))
                continue;

            benchmarkingGetAncestorsMethods(ontology);
        }
    }

    private void benchmarkingGetAncestorsMethods(TerminologyCv terminologyCv) {

        System.out.println("benchmarking "+terminologyCv+"...");

        OntologyDAG graph = new OntologyDAG(terminologyCv, terminologyService);

        Map<Long, List<String>> ancestors = new HashMap<>();
        Map<Long, long[]> ancestorsQuick = new HashMap<>();

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(terminologyCv.name());

        // COMPARE COMPUTATION DURATIONS
        Instant t = Instant.now();
        for (CvTerm cvTerm : cvTerms) {

            ancestorsQuick.put(cvTerm.getId(), graph.getAncestors(cvTerm.getId()));
        }
        System.out.println("OntologyDAG.getAncestors(): "+ChronoUnit.MILLIS.between(t, Instant.now()) + " ms");

        t = Instant.now();
        for (CvTerm cvTerm : cvTerms) {
            ancestors.put(cvTerm.getId(), TerminologyUtils.getAllAncestors(cvTerm.getAccession(), terminologyService));
        }
        System.out.println("TerminologyUtils.getAllAncestors(): "+ ChronoUnit.SECONDS.between(t, Instant.now()) + " s");


        // TEST CORRECTNESS
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

    @Ignore
    @Test
    public void checkCyclesForOntologies() throws OntologyDAG.NotFoundInternalGraphException {

        checkCyclesForOntologies(TerminologyCv.values());
    }

    private void checkCyclesForOntologies(TerminologyCv... ontologies) throws OntologyDAG.NotFoundInternalGraphException {

        for (TerminologyCv terminologyCv : ontologies) {

            OntologyDAG graph = new OntologyDAG(terminologyCv, terminologyService);

            Set<Path> cycles = graph.getAllCyclesFromTransientGraph();

            if (!cycles.isEmpty()) {
                System.out.println(terminologyCv + ": found "+cycles.size()+" cycles: "+cycles.stream()
                        .map(path -> Arrays.stream(path.toVertexArray())
                                .boxed()
                                .map(graph::getCvTermAccessionById)
                                .collect(Collectors.joining(" > ")))
                        .collect(Collectors.joining("\n")));
            }
        }
    }

    private void benchmarkingPrecomputations(TerminologyCv terminologyCv, boolean both) throws OntologyDAG.NotFoundInternalGraphException {

        System.err.println("Timing isAncestorOf() for all paths of "+terminologyCv+" graph:");
        OntologyDAG graph = new OntologyDAG(terminologyCv, terminologyService);

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
}