package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import toools.collection.bigstuff.longset.LongCursor;
import toools.collection.bigstuff.longset.LongHashSet;
import toools.collection.bigstuff.longset.LongSet;
import toools.math.MathsUtilities;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * A hierarchy of {@code CvTerm}s organised in a Directed Acyclic Graph.
 *
 * Created by fnikitin on 08.03.17.
 */
public class OntologyDAG implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(OntologyDAG.class.getSimpleName());
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("######.##");

    private final Grph graph;
    private final TerminologyCv terminologyCv;
    private final Map<String, Long> cvTermIdByAccession;
    private final Map<Long, String> cvTermAccessionById;
    private final Map<Long, LongSet> descendants;
    private final int allPathsSize;

    public OntologyDAG(TerminologyCv terminologyCv, List<CvTerm> cvTerms) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(cvTerms);
        Preconditions.checkArgument(!cvTerms.isEmpty());
        Preconditions.checkArgument(cvTerms.size() == new HashSet<>(cvTerms).size());

        this.terminologyCv = terminologyCv;

        cvTermIdByAccession = new HashMap<>(cvTerms.size());
        cvTermAccessionById = new HashMap<>(cvTerms.size());
        descendants = new HashMap<>();

        graph = new InMemoryGrph();

        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);

        allPathsSize = precomputeAllDescendants();

        logSummary();
    }

    private void logSummary() {

        StringBuilder sb = new StringBuilder("graph of "+terminologyCv);

        sb.append(": {nodes=").append(countNodes());
        sb.append(", edges=").append(countEdges());

        Collection<LongSet> ccs = graph.getConnectedComponents();
        sb.append(", connected components=").append(ccs.size());
        sb.append(", avg in-degree=").append(DECIMAL_FORMAT.format(getAverageDegree(Grph.TYPE.vertex, Grph.DIRECTION.in)));
        sb.append(", avg out-degree=").append(DECIMAL_FORMAT.format(getAverageDegree(Grph.TYPE.vertex, Grph.DIRECTION.out)));
        sb.append(", paths=").append(allPathsSize);
        sb.append("}");

        LOGGER.info(sb.toString());

        if (ccs.size() > 1) {
            LOGGER.warning(terminologyCv.name() + " has "+ccs.size()+" unconnected components");
        }
    }

    private void addCvTermNode(CvTerm cvTerm) {

        cvTermIdByAccession.put(cvTerm.getAccession(), cvTerm.getId());
        cvTermAccessionById.put(cvTerm.getId(), cvTerm.getAccession());
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

        return getAllNodes().filter(id -> graph.getInEdges(id).isEmpty());
    }

    /**
     * @return a Stream of all node ids
     */
    public Stream<Long> getAllNodes() {

        return Arrays.stream(graph.getVertices().toLongArray()).boxed();
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
    public String getCvTermAccessionById(long id) {

        if (!cvTermAccessionById.containsKey(id))
            throw new IllegalStateException("cvterm id "+id+" was not found");

        return cvTermAccessionById.get(id);
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
     * @return true if cvTermAccession was found
     */
    public boolean hasCvTermAccession(String cvTermAccession) {

        return cvTermIdByAccession.containsKey(cvTermAccession);
    }

    /**
     * @return true if queryDescendant is a descendant of queryAncestor
     */
    public boolean isAncestorOf(long queryAncestor, long queryDescendant) {

        return descendants.get(queryAncestor).contains(queryDescendant);
    }

    public boolean isAncestorOf(String queryAncestor, String queryDescendant) throws NotFoundNodeException {

        return isAncestorOf(getCvTermIdByAccession(queryAncestor), getCvTermIdByAccession(queryDescendant));
    }

    // used for benchmarking only
    boolean isAncestorOfSlow(long queryAncestor, long queryDescendant) {

        return graph.getShortestPath(queryAncestor, queryDescendant) != null;
    }

    // used for benchmarking only
    boolean isAncestorOfSlow(String queryAncestor, String queryDescendant) throws NotFoundNodeException {

        return isAncestorOfSlow(getCvTermIdByAccession(queryAncestor), getCvTermIdByAccession(queryDescendant));
    }

    /**
     * @return all the paths of this graph
     */
    public Collection<Path> getAllPaths() {
        return graph.getAllPaths();
    }

    public Map<Long, LongSet> getDescendants() {
        return descendants;
    }

    public Stream<LongSet> getConnectedComponents() {

        return graph.getConnectedComponents().stream();
    }

    public Map<String, Long> getCvTermIdByAccession() {
        return cvTermIdByAccession;
    }

    public class NotFoundNodeException extends Exception {

        public NotFoundNodeException(String accession) {

            super("CvTerm node with accession "+accession+" was not found in "+terminologyCv + " graph");
        }
    }

    // There is a deprecation in toools.math.MathsUtilities; grph.getAverageDegree() should call another method like below
    public double getAverageDegree(Grph.TYPE type, Grph.DIRECTION direction)  {

        LongArrayList l = new LongArrayList();

        for (LongCursor c : graph.getVertices())
        {
            l.add(graph.getVertexDegree(c.value, type, direction));
        }

        return MathsUtilities.computeAverage(l.toLongArray());
    }
}
