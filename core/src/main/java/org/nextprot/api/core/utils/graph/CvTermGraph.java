package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import toools.collection.bigstuff.longset.LongSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fnikitin on 23.06.17.
 */
public abstract class CvTermGraph implements DirectedGraph {

    private final DirectedGraph graph;
    private final Map<String, Long> cvTermIdByAccession;
    private final Map<Long, String> cvTermAccessionById;
    private final Map<Long, LongSet> cvTermIdAncestors;
    private final TerminologyCv terminologyCv;

    public CvTermGraph(TerminologyCv terminologyCv, TerminologyService service) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(service);

        List<CvTerm> cvTerms = service.findCvTermsByOntology(terminologyCv.name());

        this.terminologyCv = terminologyCv;

        cvTermIdByAccession = new HashMap<>(cvTerms.size());
        cvTermAccessionById = new HashMap<>(cvTerms.size());
        cvTermIdAncestors = new HashMap<>(cvTerms.size());

        graph = newGraph();

        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);

        precomputeAllAncestors();
    }

    protected abstract void precomputeAllAncestors();

    protected abstract DirectedGraph newGraph();

    protected abstract void addCvTermNode(CvTerm cvTerm);

    protected abstract void addCvTermEdges(CvTerm cvTerm);

    public TerminologyCv getTerminologyCv() {
        return terminologyCv;
    }

    /*String getCvTermAccessionById(long id);

    long getCvTermIdByAccession(String accession) throws OntologyDAG.NotFoundNodeException;

    boolean hasCvTermAccession(String cvTermAccession);

    Map<Long, LongSet> getCvTermIdAncestors();

    Map<String, Long> getCvTermIdByAccession();*/
}
