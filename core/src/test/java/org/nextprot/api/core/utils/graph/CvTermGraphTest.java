package org.nextprot.api.core.utils.graph;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ActiveProfiles({"dev"})
public class CvTermGraphTest extends CoreUnitBaseTest {

    private CvTermGraph createGraph(TerminologyCv terminologyCv, TerminologyService service) {
        return new CvTermGraph(terminologyCv, service);
    }

    @Autowired
    private TerminologyService terminologyService;

    @Test
    public void shouldCreateValidGeneOntologyGraph() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertEquals(TerminologyCv.GoMolecularFunctionCv, graph.getTerminologyCv());
        Assert.assertEquals(terminologyService.findCvTermsByOntology(TerminologyCv.GoMolecularFunctionCv.name()).size(),
                graph.countNodes());
        Assert.assertEquals(13391, graph.countEdges());
    }

    @Test
    public void nodeGO0005488ShouldHaveChildren() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(50, graph.getChildren(cvId).length);

        CvTerm cvTerm = terminologyService.findCvTermByAccession(graph.getCvTermAccessionById(cvId));

        Assert.assertEquals(cvTerm.getChildAccession().size(), graph.getChildren(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getChildren(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0030246"));
        Assert.assertTrue(Arrays.stream(graph.getChildren(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0001871"));
    }

    @Test
    public void nodeGO0005488ShouldHaveOneParent() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(1, graph.getParents(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getParents(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0003674"));
    }

    @Test
    public void nodeGO0000006ShouldBeALeaf() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int cvId = graph.getCvTermIdByAccession("GO:0000006");

        Assert.assertEquals(0, graph.getChildren(cvId).length);
    }

    @Test
    public void geneOntologyShouldContainOneRoot() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int[] roots = graph.getSources();
        Assert.assertEquals(1, roots.length);

        List<String> accessions = new ArrayList<>();
        for (int i=0 ; i<roots.length ; i++) {

            accessions.add(graph.getCvTermAccessionById(roots[i]));
        }

        Assert.assertEquals(1, accessions.size());
        Assert.assertEquals("GO:0003674", accessions.get(0));
    }

    @Test
    public void GO0005488shouldBeAncestorOfGO0005488() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int ancestorId = graph.getCvTermIdByAccession("GO:0005488");
        int descendantId = graph.getCvTermIdByAccession("GO:0051378");

        Assert.assertTrue(graph.isAncestorOf(ancestorId, descendantId));
        Assert.assertTrue(graph.isDescendantOf(descendantId, ancestorId));
    }

    @Test
    public void roudoudouShouldNotExistInOntology() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertFalse(graph.hasCvTermAccession("roudoudou"));
    }

    @Test
    public void GO0042947ShouldNotExistInOntology() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertTrue(graph.hasCvTermAccession("GO:0042947"));
    }

    @Test
    public void ancestorSubgraph() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoBiologicalProcessCv, terminologyService);

        CvTermGraph ancestorGraph = graph.calcAncestorSubgraph(graph.getCvTermIdByAccession("GO:0050789"));

        Set<String> nodes = IntStream.of(ancestorGraph.getNodes())
                .mapToObj(graph::getCvTermAccessionById)
                .collect(Collectors.toSet());

        Assert.assertEquals(Sets.newHashSet("GO:0050789", "GO:0065007", "GO:0008150"), nodes);

        List<List<String>> expectedEdges = Arrays.asList(
                Arrays.asList("GO:0065007", "GO:0050789"),
                Arrays.asList("GO:0008150", "GO:0050789"),
                Arrays.asList("GO:0008150", "GO:0065007")
        );

        for (int e : ancestorGraph.getEdges()) {

            List<String> edgeNodes = Arrays.asList(
                    graph.getCvTermAccessionById(ancestorGraph.getTailNode(e)),
                    graph.getCvTermAccessionById(ancestorGraph.getHeadNode(e))
            );

            Assert.assertTrue(expectedEdges.contains(edgeNodes));
        }
    }

    @Test
    public void testBloodTermAncestorSubgraph() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.NextprotAnatomyCv, terminologyService);

        CvTermGraph ancestorGraph = graph.calcAncestorSubgraph(graph.getCvTermIdByAccession("TS-0079"));

        Assert.assertEquals("TS-0079 ancestor graph", ancestorGraph.graph.getGraphLabel());

        Set<String> nodes = IntStream.of(ancestorGraph.getNodes())
                .mapToObj(graph::getCvTermAccessionById)
                .collect(Collectors.toSet());

        Assert.assertEquals(Sets.newHashSet("TS-0079", "TS-0449", "TS-1297", "TS-2018", "TS-2088", "TS-2101", "TS-2178"), nodes);

        List<List<String>> expectedEdges = Arrays.asList(
                Arrays.asList("TS-1297", "TS-0079"),
                Arrays.asList("TS-0449", "TS-0079"),
                Arrays.asList("TS-2101", "TS-0079"),
                Arrays.asList("TS-2018", "TS-0449"),
                Arrays.asList("TS-2088", "TS-1297"),
                Arrays.asList("TS-2088", "TS-2018"),
                Arrays.asList("TS-2178", "TS-2101"),
                Arrays.asList("TS-2178", "TS-2088")

        );

        for (int e : ancestorGraph.getEdges()) {

            List<String> edgeNodes = Arrays.asList(
                    graph.getCvTermAccessionById(ancestorGraph.getTailNode(e)),
                    graph.getCvTermAccessionById(ancestorGraph.getHeadNode(e))
            );

            Assert.assertTrue(expectedEdges.contains(edgeNodes));
        }
    }

    @Test
    public void testBloodTermAncestorSubgraphView() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.NextprotAnatomyCv, terminologyService);

        CvTermGraph ancestorGraph = graph.calcAncestorSubgraph(graph.getCvTermIdByAccession("TS-0079"));

        CvTermGraph.View view = ancestorGraph.toView();

        List<CvTermGraph.View.Node> nodes = view.getNodes();

        Assert.assertEquals("TS-0079 ancestor graph", view.getLabel());
        Assert.assertEquals(7, nodes.size());
        Assert.assertEquals(8, view.getEdges().size());
        Assert.assertTrue(nodes.stream().anyMatch(n -> n.getAccession().equals("TS-0079") && n.getName().equals("Blood")));
        Assert.assertTrue(nodes.stream().anyMatch(n -> n.getAccession().equals("TS-0449") && n.getName().equals("Hematopoietic and immune systems")));
    }

    @Test
    public void cvterm1071ShouldHaveAnAccession() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertEquals("GO:0000006", graph.getCvTermAccessionById(1071));
    }

    @Test
    public void cvterm1071ShouldHaveAName() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertEquals("high-affinity zinc uptake transmembrane transporter activity", graph.getCvTermNameById(1071));
    }

    @Test
    public void testUniprotFamilyGraph() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.UniprotFamilyCv, terminologyService);
        Assert.assertEquals(9793, graph.countNodes());
        Assert.assertEquals(2091, graph.countEdges());
    }

    @Test
    public void testBloodTermDescendantSubgraphView() throws Exception {

        CvTermGraph graph = createGraph(TerminologyCv.GoBiologicalProcessCv, terminologyService);

        CvTermGraph descendantSubgraph = graph.calcDescendantSubgraph(graph.getCvTermIdByAccession("GO:0043491"));

        CvTermGraph.View view = descendantSubgraph.toView();

        Assert.assertEquals("GO:0043491 descendant graph", view.getLabel());
        Assert.assertEquals(4, view.getNodes().size());
        Assert.assertEquals(5, view.getEdges().size());
        Assert.assertEquals(26584, view.getEdges().get(0).getTail());
        Assert.assertEquals(26586, view.getEdges().get(0).getHead());
        Assert.assertEquals(20191, view.getEdges().get(1).getTail());
        Assert.assertEquals(26586, view.getEdges().get(1).getHead());
        Assert.assertEquals(26584, view.getEdges().get(2).getTail());
        Assert.assertEquals(26585, view.getEdges().get(2).getHead());
        Assert.assertEquals(20191, view.getEdges().get(3).getTail());
        Assert.assertEquals(26585, view.getEdges().get(3).getHead());
        Assert.assertEquals(20191, view.getEdges().get(4).getTail());
        Assert.assertEquals(26584, view.getEdges().get(4).getHead());
    }

    @Test
    public void testSubgraphMetadata() throws Exception {

        CvTermGraph graph = terminologyService.findCvTermGraph(TerminologyCv.EvidenceCodeOntologyCv);
        Assert.assertTrue(graph.hasCvTermAccession("ECO:0000269"));
        Assert.assertTrue(graph.hasCvTermAccession("ECO:0001186"));

        CvTerm experimentalEvidenceUsedInManualAssertionTerm = terminologyService.findCvTermByAccession("ECO:0000269");
        CvTermGraph sg = graph.calcDescendantSubgraph(experimentalEvidenceUsedInManualAssertionTerm.getId().intValue());

        Assert.assertTrue(sg.hasCvTermAccession("ECO:0000269"));
        Assert.assertTrue(sg.hasCvTermAccession("ECO:0001186"));
    }
}