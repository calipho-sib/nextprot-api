package org.nextprot.api.core.utils.graph;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.graph.DirectedGraph;
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

@ActiveProfiles({"dev","cache"})
public abstract class BaseCvTermGraphTest extends CoreUnitBaseTest {

    protected abstract BaseCvTermGraph createGraph(TerminologyCv terminologyCv, TerminologyService service);

    @Autowired
    private TerminologyService terminologyService;

    @Test
    public void shouldCreateValidGeneOntologyGraph() throws Exception {

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertEquals(TerminologyCv.GoMolecularFunctionCv, graph.getTerminologyCv());
        Assert.assertEquals(10543, graph.countNodes());
        Assert.assertEquals(12797, graph.countEdges());
    }

    @Test
    public void nodeGO0005488ShouldHaveChildren() throws Exception {

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

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

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int cvId = graph.getCvTermIdByAccession("GO:0005488");

        Assert.assertEquals(1, graph.getParents(cvId).length);
        Assert.assertTrue(Arrays.stream(graph.getParents(cvId)).boxed()
                .map(graph::getCvTermAccessionById).collect(Collectors.toSet()).contains("GO:0003674"));
    }

    @Test
    public void nodeGO0000006ShouldBeALeaf() throws Exception {

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int cvId = graph.getCvTermIdByAccession("GO:0000006");

        Assert.assertEquals(0, graph.getChildren(cvId).length);
    }

    @Test
    public void geneOntologyShouldContainOneRoot() throws Exception {

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

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

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        int ancestorId = graph.getCvTermIdByAccession("GO:0005488");
        int descendantId = graph.getCvTermIdByAccession("GO:0051378");

        Assert.assertTrue(graph.isAncestorOf(ancestorId, descendantId));
        Assert.assertTrue(graph.isDescendantOf(descendantId, ancestorId));
    }

    @Test
    public void roudoudouShouldNotExistInOntology() throws Exception {

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoMolecularFunctionCv, terminologyService);

        Assert.assertFalse(graph.hasCvTermAccession("roudoudou"));
    }

    @Test
    public void ancestorSubgraph() throws Exception {

        BaseCvTermGraph graph = createGraph(TerminologyCv.GoBiologicalProcessCv, terminologyService);

        DirectedGraph ancestorGraph = graph.calcAncestorSubgraph(graph.getCvTermIdByAccession("GO:0050789"));

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
}