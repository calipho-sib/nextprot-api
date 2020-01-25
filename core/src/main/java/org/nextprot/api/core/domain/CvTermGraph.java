package org.nextprot.api.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.commons.graph.IntGraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A hierarchy of {@code CvTerm} ids organised in a Graph.
 *
 * Backing graph of cv terms as ints node as the maximum id is 225719 !!
 *
 * Created by fnikitin on 23.06.17.
 */
public class CvTermGraph implements Serializable {

    private static final long serialVersionUID = 4L;

    private final static Logger LOGGER = Logger.getLogger(CvTermGraph.class.getSimpleName());

    private static final String ACCESSION_KEY = "accession";
    private static final String NAME_KEY = "name";

    private final TerminologyCv terminologyCv;
    protected final IntGraph graph;

    public CvTermGraph(TerminologyCv terminologyCv, List<CvTerm> cvTerms) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(cvTerms);

        this.terminologyCv = terminologyCv;

        graph = new IntGraph(terminologyCv.name() + " graph");
        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);
    }

    CvTermGraph(TerminologyCv terminologyCv, IntGraph graph) {

        Preconditions.checkNotNull(graph);

        this.graph = graph;
        this.terminologyCv = terminologyCv;

        if (graph.getGraphLabel().length() == 0) {
            graph.setGraphLabel(terminologyCv.name() + " graph");
        }
    }

    private void addCvTermNode(CvTerm cvTerm) {

        int nodeId = Math.toIntExact(cvTerm.getId());

        graph.addNode(nodeId);
        graph.addNodeMetadata(nodeId, ACCESSION_KEY, cvTerm.getAccession());
        graph.addNodeMetadata(nodeId, NAME_KEY, cvTerm.getName());

    }

    private void addCvTermEdges(CvTerm cvTerm) {

        List<CvTerm.TermAccessionRelation> parentAccessions = cvTerm.getAncestorsRelations();

        if (parentAccessions != null) {
            parentAccessions.forEach(parent -> {
                try {
                    int eid = graph.addEdge(getCvTermIdByAccession(parent.getTermAccession()), Math.toIntExact(cvTerm.getId()));
                    graph.setEdgeLabel(eid, parent.getRelationType());
                } catch (NotFoundNodeException e) {
                    LOGGER.warning(cvTerm.getAccession()+" cannot connect to unknown node parent: "+e.getMessage());
                }
            });
        }
    }

    /**
     * @return the TerminologyCv of this graph of CvTerms
     */
    public TerminologyCv getTerminologyCv() {

        return terminologyCv;
    }

    /**
     * @return the CvTerm accession
     */
    public String getCvTermAccessionById(int id) {

        return graph.getNodeMetadataValue(id, ACCESSION_KEY);
    }

    /**
     * @return the CvTerm with given id
     */
    public String getCvTermNameById(int id) {

        return graph.getNodeMetadataValue(id, NAME_KEY);
    }

    /**
     * @return the id of cvterm with given accession
     */
    public int getCvTermIdByAccession(String accession) throws NotFoundNodeException {

        int cvTerm = graph.getNodeFromMetadata(accession);

        if (cvTerm == -1)
            throw this.new NotFoundNodeException(accession);

        return cvTerm;
    }

    /**
     * @return true if cvTermAccession was found
     */
    public boolean hasCvTermAccession(String cvTermAccession) {

        return graph.getNodeFromMetadata(cvTermAccession) != -1;
    }

    int[] getSources() {

        return graph.getSources();
    }

    public int[] getNodes() {

        return graph.getNodes();
    }

    public int countNodes() {

        return graph.countNodes();
    }

    public int countEdges() {

        return graph.countEdges();
    }

    public boolean isAncestorOf(int queryAncestor, int queryDescendant) {

        return graph.isAncestorOf(queryAncestor, queryDescendant);
    }

    public boolean isDescendantOf(int queryDescendant, int queryAncestor) {

        return graph.isDescendantOf(queryDescendant, queryAncestor);
    }

    public int[] getAncestors(int cvTermId) {

        return graph.getAncestors(cvTermId);
    }

    public int[] getDescendants(int cvTermId) {

        return graph.getDescendants(cvTermId);
    }

    public CvTermGraph calcAncestorSubgraph(int cvTermId) {

        return buildSubgraph(cvTermId, (id) -> graph.getAncestors(id), "ancestor");
    }

    public CvTermGraph calcDescendantSubgraph(int cvTermId) {
        return calcDescendantSubgraph(cvTermId, 0);
    }

    public CvTermGraph calcDescendantSubgraph(int cvTermId, int maxDepth) {

        return buildSubgraph(cvTermId, (id) -> graph.getDescendants(id, maxDepth), "descendant");
    }

    private CvTermGraph buildSubgraph(int cvTermId, Function<Integer, int[]> func, String type) {

        int[] nodes = func.apply(cvTermId);
        int[] subgraphNodes = new int[nodes.length+1];

        subgraphNodes[0] = cvTermId;
        System.arraycopy(nodes, 0, subgraphNodes, 1, nodes.length);

        IntGraph sg = graph.calcSubgraph(subgraphNodes);

        sg.setGraphLabel(sg.getNodeMetadataValue(cvTermId, ACCESSION_KEY)+" " + type + " graph");

        return new CvTermGraph(terminologyCv, sg);
    }
    
    public String getEdgeLabel(int tailNode, int headNode) {
    	int edge = graph.getEdge(tailNode, headNode);
    	return graph.getEdgeLabel(edge);
    }

    public int[] getParents(int cvTermId) {

        return graph.getPredecessors(cvTermId);
    }

    public int[] getChildren(int cvTermId) {

        return graph.getSuccessors(cvTermId);
    }

    public int[] getEdges() {
        return graph.getEdges();
    }

    public int getTailNode(int edge) {
        return graph.getTailNode(edge);
    }

    public int getHeadNode(int edge) {
        return graph.getHeadNode(edge);
    }

    public View toView() {

        View view = new View();

        view.setLabel(graph.getGraphLabel());

        for (int nid : graph.getNodes()) {
            View.Node node = new View.Node();
            node.setData(nid, graph.getNodeMetadataValue(nid, ACCESSION_KEY), graph.getNodeMetadataValue(nid, NAME_KEY));
            view.addNode(node);
        }

        for (int eid : graph.getEdges()) {
            View.Edge edge = new View.Edge();
            edge.setData(eid, graph.getTailNode(eid), graph.getHeadNode(eid));
            edge.setLabel(graph.getEdgeLabel(eid));
            view.addEdge(edge);
        }

        return view;
    }

    /**
     * Thrown if no nodes map the given cvterm accession
     */
    public class NotFoundNodeException extends Exception {

        public NotFoundNodeException(String accession) {

            super("CvTerm node with accession "+accession+" was not found in "+terminologyCv + " graph");
        }
    }

    public static class View {

        private String label;
        private List<Node> nodes = new ArrayList<>();
        private List<Edge> edges = new ArrayList<>();

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void addNode(Node node) {
            this.nodes.add(node);
        }

        public List<Edge> getEdges() {
            return edges;
        }

        public void addEdge(Edge edge) {
            this.edges.add(edge);
        }

        public static class Node {

            private int id;
            private String accession;
            private String name;
            private Long relevantFor;

            public int getId() {
                return id;
            }

            public String getAccession() {
                return accession;
            }

            public String getName() {
                return name;
            }

            public void setData(int id, String accession, String name) {
                this.id = id;
                this.accession = accession;
                this.name = name;
            }


            @JsonInclude(JsonInclude.Include.NON_NULL)
            public Long getRelevantFor() { return relevantFor; }
            public void setRelevantFor(Long relevantFor) { this.relevantFor = relevantFor; }


        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Edge {

            private int id;
            private int tail;
            private int head;
            private String label;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getTail() {
                return tail;
            }

            public int getHead() {
                return head;
            }

            public void setData(int id, int tail, int head) {
                this.id = id;
                this.tail = tail;
                this.head = head;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

        }

    }
}
