package org.nextprot.api.core.utils.graph;

import com.google.common.collect.Sets;
import grph.Grph;
import grph.path.Path;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
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
    public void ancestorSubgraph() throws Exception {

        OntologyDAG graph = new OntologyDAG(TerminologyCv.GoBiologicalProcessCv, terminologyService);

        Grph ancestorGraph = graph.getAncestorSubgraph(graph.getCvTermIdByAccession("GO:0050789"));

        Set<String> nodes = LongStream.of(ancestorGraph.getVertices().toLongArray())
                .mapToObj(graph::getCvTermAccessionById)
                .collect(Collectors.toSet());

        Assert.assertEquals(Sets.newHashSet("GO:0050789", "GO:0065007", "GO:0008150"), nodes);

        List<String[]> expectedEdges = Arrays.asList(
                new String[] {"GO:0065007", "GO:0050789"},
                new String[] {"GO:0008150", "GO:0050789"},
                new String[] {"GO:0008150", "GO:0065007"});

        for (long e : ancestorGraph.getEdges().toLongArray()) {

            List<String> edge = ancestorGraph.getVerticesIncidentToEdge(e).toLongArrayList().stream()
                    .map(graph::getCvTermAccessionById)
                    .collect(Collectors.toList());

            Assert.assertTrue(expectedEdges.contains(edge));
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