package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.graph.DirectedGraph;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Backing graph of cv terms as ints node as the maximum id is 225719 !!
 *
 * Created by fnikitin on 23.06.17.
 */
abstract class BaseCvTermGraph implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(BaseCvTermGraph.class.getSimpleName());

    private final TerminologyCv terminologyCv;
    protected final DirectedGraph graph;

    private final Map<String, Integer> cvTermIdByAccession;
    private final Map<Integer, String> cvTermAccessionById;

    public BaseCvTermGraph(TerminologyCv terminologyCv, TerminologyService service, Supplier<DirectedGraph> graphSupplier) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(service);

        List<CvTerm> cvTerms = service.findCvTermsByOntology(terminologyCv.name());

        this.terminologyCv = terminologyCv;

        cvTermIdByAccession = new HashMap<>(cvTerms.size());
        cvTermAccessionById = new HashMap<>(cvTerms.size());

        graph = graphSupplier.get();

        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);
    }

    private void addCvTermNode(CvTerm cvTerm) {

        int nodeId = Math.toIntExact(cvTerm.getId());

        graph.addNode(nodeId);

        cvTermIdByAccession.put(cvTerm.getAccession(), nodeId);
        cvTermAccessionById.put(nodeId, cvTerm.getAccession());
    }

    private void addCvTermEdges(CvTerm cvTerm) {

        List<String> parentAccessions = cvTerm.getAncestorAccession();

        if (parentAccessions != null) {
            parentAccessions.forEach(parent -> {
                try {
                    graph.addEdge(getCvTermIdByAccession(parent), Math.toIntExact(cvTerm.getId()));
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
     * @return the CvTerm with given id
     */
    public String getCvTermAccessionById(int id) {

        if (!cvTermAccessionById.containsKey(id))
            throw new IllegalStateException("cvterm id "+id+" was not found");

        return cvTermAccessionById.get(id);
    }

    /**
     * @return the id of cvterm with given accession
     */
    public int getCvTermIdByAccession(String accession) throws NotFoundNodeException {

        if (!cvTermIdByAccession.containsKey(accession))
            throw this.new NotFoundNodeException(accession);

        return cvTermIdByAccession.get(accession);
    }

    /**
     * @return true if cvTermAccession was found
     */
    public boolean hasCvTermAccession(String cvTermAccession) {

        return cvTermIdByAccession.containsKey(cvTermAccession);
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

    public DirectedGraph calcAncestorSubgraph(int cvTermId) {

        return graph.calcAncestorSubgraph(cvTermId);
    }

    public int[] getParents(int cvTermId) {

        return graph.getPredecessors(cvTermId);
    }

    public int[] getChildren(int cvTermId) {

        return graph.getSuccessors(cvTermId);
    }

    /**
     * Thrown if no nodes map the given cvterm accession
     */
    public class NotFoundNodeException extends Exception {

        public NotFoundNodeException(String accession) {

            super("CvTerm node with accession "+accession+" was not found in "+terminologyCv + " graph");
        }
    }
}
