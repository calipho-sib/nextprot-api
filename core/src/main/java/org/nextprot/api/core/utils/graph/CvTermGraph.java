package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A graph of CvTerm nodes with relations between them.
 *
 * Created by fnikitin on 08.03.17.
 */
public class CvTermGraph implements Serializable {

    private final TerminologyCv terminologyCv;
    private final Grph graph;
    private final Map<Long, CvTerm> cvTermById;
    private final Map<String, Long> cvTermIdByAccession; // used to connect cv terms

    public CvTermGraph(TerminologyCv terminologyCv, List<CvTerm> cvTerms) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(cvTerms);
        Preconditions.checkArgument(!cvTerms.isEmpty());

        this.terminologyCv = terminologyCv;

        cvTermById = new HashMap<>();
        cvTermIdByAccession = new HashMap<>();
        graph = new InMemoryGrph();

        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);

        if (graph.getNumberOfVertices() != cvTermById.keySet().size())
            throw new IllegalStateException("inconsistent state when creating CvTerm graph");
    }

    private void addCvTermNode(CvTerm cvTerm) {

        cvTermById.put(cvTerm.getId(), cvTerm);
        cvTermIdByAccession.put(cvTerm.getAccession(), cvTerm.getId());
        graph.addVertex(cvTerm.getId());
    }

    private void addCvTermEdges(CvTerm cvTerm) {

        List<String> parentAccessions = cvTerm.getAncestorAccession();
        List<String> childrenAccessions = cvTerm.getChildAccession();

        if (parentAccessions != null) {
            parentAccessions.forEach(parent -> graph.addDirectedSimpleEdge(
                    cvTermIdByAccession.get(parent),
                    cvTerm.getId())
            );
        }

        if (childrenAccessions != null) {
            childrenAccessions.forEach(child -> graph.addDirectedSimpleEdge(
                    cvTerm.getId(),
                    cvTermIdByAccession.get(child))
            );
        }
    }

    /**
     * @return the TerminologyCv of this graph of CvTerms
     */
    public TerminologyCv getTerminologyCv() {

        return terminologyCv;
    }

    /**
     * @return a Stream of cvterm ids that are parents of cvTermId
     */
    public Stream<Long> getParents(long cvTermId) {

        return Arrays.stream(graph.getInNeighbors(cvTermId).toLongArray()).boxed();
    }

    /**
     * @return a Stream of cvterm ids that are children of cvTermId
     */
    public Stream<Long> getChildren(long cvTermId) {

        return Arrays.stream(graph.getOutNeighbors(cvTermId).toLongArray()).boxed();
    }

    /**
     * @return a Stream of root(s) ids of the graph
     */
    public Stream<Long> getRoots() {

        return Arrays.stream(graph.getVertices().toLongArray())
                .filter(id -> graph.getInEdges(id).isEmpty())
                .boxed();
    }

    /**
     * @return the total number of graph nodes
     */
    public long countNodes() {

        return graph.getNumberOfVertices();
    }

    /**
     * @return the total number of graph edges
     */
    public long countEdges() {

        return graph.getSize();
    }

    /**
     * @return the CvTerm with given id
     */
    public CvTerm getCvTermById(long id) {

        if (!cvTermById.containsKey(id))
            throw new IllegalStateException("cvterm id "+id+" was not found");

        return cvTermById.get(id);
    }

    /**
     * @return the id of cvterm with given accession
     */
    public long getCvTermIdByAccession(String accession) {

        if (!cvTermIdByAccession.containsKey(accession))
            throw new IllegalStateException("cvterm accession "+accession+" was not found");


        return cvTermIdByAccession.get(accession);
    }

    public Map<String, CvTerm> exportMap() {

        Map<String, CvTerm> map = new HashMap<>();

        for (CvTerm cvTerm : cvTermById.values()) {

            map.put(cvTerm.getAccession(), cvTerm);
        }

        return map;
    }
}
