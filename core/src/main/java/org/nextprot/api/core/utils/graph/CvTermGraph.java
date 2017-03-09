package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import toools.collection.bigstuff.longset.LongHashSet;
import toools.collection.bigstuff.longset.LongSet;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A graph of CvTerm nodes with relations between them.
 *
 * Created by fnikitin on 08.03.17.
 */
public class CvTermGraph implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(CvTermGraph.class.getSimpleName());

    private final Grph graph;
    private final TerminologyCv terminologyCv;
    private final Map<Long, CvTerm> cvTermById;
    private final Map<String, Long> cvTermIdByAccession;
    private final Map<Long, LongSet> descendants;

    public CvTermGraph(TerminologyCv terminologyCv, List<CvTerm> cvTerms) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(cvTerms);
        Preconditions.checkArgument(!cvTerms.isEmpty());
        Preconditions.checkArgument(cvTerms.size() == new HashSet<>(cvTerms).size());

        this.terminologyCv = terminologyCv;

        cvTermById = new HashMap<>();
        cvTermIdByAccession = new HashMap<>();
        descendants = new HashMap<>();

        graph = new InMemoryGrph();

        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);

        int pathNumber = precomputeAllDescendants();
        sanityCheck();
        summary(pathNumber);
    }

    private void sanityCheck() {

        if (graph.getNumberOfVertices() != cvTermById.keySet().size()) {
            throw new IllegalStateException(terminologyCv.name() + " inconsistent graph state: invalid number of vertices");
        }
    }

    private void summary(int pathNumber) {

        StringBuilder sb = new StringBuilder("graph of "+terminologyCv);

        sb.append(": nodes=").append(countNodes());
        sb.append(", edges=").append(countEdges());

        Collection<LongSet> ccs = graph.getConnectedComponents();
        sb.append(", connected components=").append(ccs.size());
        sb.append(", in-degrees=").append(graph.getDegreeDistribution(Grph.TYPE.vertex, Grph.DIRECTION.in).toString(true, false));
        sb.append("out-degrees=").append(graph.getDegreeDistribution(Grph.TYPE.vertex, Grph.DIRECTION.out).toString(true, false));
        sb.append("paths=").append(pathNumber);

        LOGGER.info(sb.toString());

        if (ccs.size() > 1) {
            LOGGER.warning(terminologyCv.name() + " has "+ccs.size()+" unconnected components");
        }
    }

    private void addCvTermNode(CvTerm cvTerm) {

        cvTermById.put(cvTerm.getId(), cvTerm);
        cvTermIdByAccession.put(cvTerm.getAccession(), cvTerm.getId());
        graph.addVertex(cvTerm.getId());
        descendants.put(cvTerm.getId(), new LongHashSet());
    }

    private void addCvTermEdges(CvTerm cvTerm) {

        List<String> parentAccessions = cvTerm.getAncestorAccession();

        if (parentAccessions != null) {
            parentAccessions.forEach(parent -> {
                try {
                    graph.addDirectedSimpleEdge(getCvTermIdByAccession(parent), cvTerm.getId());
                } catch (NotFoundNodeException e) {
                    LOGGER.warning(cvTerm.getAccession()+" cannot connect to unknown node parent: "+e.getMessage() + ", CvTerm:\n"+cvTerm);
                }
            });
        }
    }

    private int precomputeAllDescendants() {

        Collection<Path> paths = graph.getAllPaths();

        for (Path path : paths) {

            long source = path.getSource();
            for (long i=0 ; i<path.getNumberOfVertices() ; i++) {

                long vertex  = path.getVertexAt(i);

                if (vertex != source) {
                    descendants.get(source).add(vertex);
                }
            }
        }
        return paths.size();
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
    public long getCvTermIdByAccession(String accession) throws NotFoundNodeException {

        if (!cvTermIdByAccession.containsKey(accession))
            throw new NotFoundNodeException(accession);

        return cvTermIdByAccession.get(accession);
    }

    /**
     *
     * @param queryAncestor the node
     * @param queryDescendant
     * @return true if node2 is a descendant of node1
     */
    public boolean isAncestorOf(long queryAncestor, long queryDescendant) {

        return descendants.get(queryAncestor).contains(queryDescendant);
    }

    public boolean isAncestorOf(String queryAncestor, String queryDescendant) throws NotFoundNodeException {

        return isAncestorOf(getCvTermIdByAccession(queryAncestor), getCvTermIdByAccession(queryDescendant));
    }

    public boolean isAncestorOfSlow(String queryAncestor, String queryDescendant) throws NotFoundNodeException {

        Path path = graph.getShortestPath(getCvTermIdByAccession(queryAncestor), getCvTermIdByAccession(queryDescendant));
        return path != null;
    }

    public Map<String, CvTerm> exportMap() {

        Map<String, CvTerm> map = new HashMap<>();

        for (CvTerm cvTerm : cvTermById.values()) {

            map.put(cvTerm.getAccession(), cvTerm);
        }

        return map;
    }

    public class NotFoundNodeException extends Exception {

        public NotFoundNodeException(String accession) {

            super("CvTerm node with accession "+accession+" was not found in "+terminologyCv + " graph");
        }
    }
}
