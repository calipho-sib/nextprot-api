package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
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
 * A hierarchy of {@code CvTerm} ids organised in a Directed Acyclic Graph.
 *
 * <h4>Warning</h4>
 * The graph data structure is backed by an instance of Grph that is a transient field.
 *
 * A call of a method depending on Grph field on a deserialized OntologyDAG instance (graph not accessible anymore)
 * will throw a NotFoundInternalGrphException.
 *
 * To help using this object, methods depending on this field were named with suffix "..FromTransientGraph()" and a
 * dedicated method {@code isTransientGraphAvailable()} was added to test for {@code transientGraph} eligibility.
 *
 * Created by fnikitin on 08.03.17.
 */
public class OntologyDAG implements Serializable {

    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(OntologyDAG.class.getSimpleName());
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("######.##");

    private transient final Grph transientGraph;
    private final TerminologyCv terminologyCv;
    private final Map<String, Long> cvTermIdByAccession;
    private final Map<Long, String> cvTermAccessionById;
    private final Map<Long, LongSet> cvTermIdAncestors;
    private final int allPathsSize;

    public OntologyDAG(TerminologyCv terminologyCv, TerminologyService service) {

        Preconditions.checkNotNull(terminologyCv);
        Preconditions.checkNotNull(service);

        List<CvTerm> cvTerms = service.findCvTermsByOntology(terminologyCv.name());

        this.terminologyCv = terminologyCv;

        cvTermIdByAccession = new HashMap<>(cvTerms.size());
        cvTermAccessionById = new HashMap<>(cvTerms.size());
        cvTermIdAncestors = new HashMap<>(cvTerms.size());

        transientGraph = new InMemoryGrph();

        cvTerms.forEach(this::addCvTermNode);
        cvTerms.forEach(this::addCvTermEdges);

        allPathsSize = precomputeAllAncestors();
    }

    private void addCvTermNode(CvTerm cvTerm) {

        cvTermIdByAccession.put(cvTerm.getAccession(), cvTerm.getId());
        cvTermAccessionById.put(cvTerm.getId(), cvTerm.getAccession());
        transientGraph.addVertex(cvTerm.getId());
        cvTermIdAncestors.put(cvTerm.getId(), new LongHashSet());
    }

    private void addCvTermEdges(CvTerm cvTerm) {

        List<String> parentAccessions = cvTerm.getAncestorAccession();

        if (parentAccessions != null) {
            parentAccessions.forEach(parent -> {
                try {
                    transientGraph.addDirectedSimpleEdge(getCvTermIdByAccession(parent), cvTerm.getId());
                } catch (NotFoundNodeException e) {
                    LOGGER.warning(cvTerm.getAccession()+" cannot connect to unknown node parent: "+e.getMessage());
                }
            });
        }
    }

    private int precomputeAllAncestors() {

        Collection<Path> paths = transientGraph.getAllPaths();

        for (Path path : paths) {

            long dest = path.getDestination();

            if (path.getNumberOfVertices() > 1) {
                for (long i = 0; i < path.getNumberOfVertices() - 1; i++) {

                    cvTermIdAncestors.get(dest).add(path.getVertexAt(i));
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
     * @return a Stream of root(s) ids of the graph
     */
    Stream<Long> getRoots() {

        return getAllNodes().filter(id -> transientGraph.getInEdges(id).isEmpty());
    }

    /**
     * @return a Stream of all node ids
     */
    public Stream<Long> getAllNodes() {

        return cvTermIdAncestors.keySet().stream();
    }

    /**
     * @return the total number of graph nodes
     */
    public long countNodes() {

        return cvTermIdAncestors.size();
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

        return cvTermIdAncestors.get(queryDescendant).contains(queryAncestor);
    }

    /**
     * @return true if queryDescendant is a descendant of queryAncestor
     */
    public boolean isChildOf(long queryDescendant, long queryAncestor) {

        return cvTermIdAncestors.get(queryDescendant).contains(queryAncestor);
    }

    // used for benchmarking only
    boolean isAncestorOfSlow(long queryAncestor, long queryDescendant) {

        return transientGraph.getShortestPath(queryAncestor, queryDescendant) != null;
    }

    /**
     * @return descendants of the given cvterm id
     */
    public long[] getAncestors(long cvTermId) {
        return cvTermIdAncestors.get(cvTermId).toLongArray();
    }

    /**
     * @return ancestors map of all cvterm nodes
     */
    public Map<Long, LongSet> getCvTermIdAncestors() {
        return cvTermIdAncestors;
    }

    /**
     * @return the mappings of cv term accession to id
     */
    public Map<String, Long> getCvTermIdByAccession() {
        return cvTermIdByAccession;
    }

    /**
     * @return true if grph instance exist (used to check if specific method name with suffix "FromGrph" are callable)
     */
    public boolean isTransientGraphAvailable() {

        return transientGraph != null;
    }

    /**
     * @return the set of all path containing a cycle
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    public Set<Path> getAllCyclesFromTransientGraph() throws NotFoundInternalGraphException {

        checkTransientGraphAvailability();

        return transientGraph.getAllCycles();
    }

    /**
     * @return a Stream of cvterm ids that are parents of cvTermId
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    public long[] getParentsFromTransientGraph(long cvTermId) throws NotFoundInternalGraphException {

        checkTransientGraphAvailability();

        return transientGraph.getInNeighbors(cvTermId).toLongArray();
    }

    /**
     * @return a Stream of cvterm ids that are children of cvTermId
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    public long[] getChildrenFromTransientGraph(long cvTermId) throws NotFoundInternalGraphException {

        checkTransientGraphAvailability();

        return transientGraph.getOutNeighbors(cvTermId).toLongArray();
    }
    /**
     * @return the total number of graph edges
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    public long countEdgesFromTransientGraph() throws NotFoundInternalGraphException {

        checkTransientGraphAvailability();

        return transientGraph.getSize();
    }

    /**
     * @return all the paths of this graph
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    public Collection<Path> getAllPathsFromTransientGraph() throws NotFoundInternalGraphException {

        checkTransientGraphAvailability();

        return transientGraph.getAllPaths();
    }

    /**
     * @return the connected components of the graph
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    public Stream<LongSet> getConnectedComponentsFromTransientGraph() throws NotFoundInternalGraphException {

        checkTransientGraphAvailability();

        return transientGraph.getConnectedComponents().stream();
    }

    /**
     * @return the average degree of the graph
     * @throws NotFoundInternalGraphException if internal graph is missing
     */
    // There is a deprecation in toools.math.MathsUtilities; grph.getAverageDegree() should call another method like below
    public double getAverageDegreeFromTransientGraph(Grph.TYPE type, Grph.DIRECTION direction) throws NotFoundInternalGraphException {

        LongArrayList l = new LongArrayList();

        for (LongCursor c : transientGraph.getVertices())
        {
            l.add(transientGraph.getVertexDegree(c.value, type, direction));
        }

        return MathsUtilities.computeAverage(l.toLongArray());
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("graph of "+terminologyCv);

        sb.append(": {nodes=").append(countNodes());
        sb.append(", edges=").append(transientGraph.getSize());

        Collection<LongSet> ccs = transientGraph.getConnectedComponents();
        sb.append(", connected components=").append(ccs.size());
        sb.append(", avg degree=").append(DECIMAL_FORMAT.format(transientGraph.getAverageDegree()));
        sb.append(", paths=").append(allPathsSize);
        sb.append("}");

        return sb.toString();
    }

    private void checkTransientGraphAvailability() throws NotFoundInternalGraphException {

        if (transientGraph == null)
            throw new NotFoundInternalGraphException();
    }

    /**
     * Thrown if no nodes map the given cvterm accession
     */
    public class NotFoundNodeException extends Exception {

        public NotFoundNodeException(String accession) {

            super("CvTerm node with accession "+accession+" was not found in "+terminologyCv + " graph");
        }
    }

    /**
     * Thrown when transient graph is not available anymore from methods supposed to need it
     */
    public class NotFoundInternalGraphException extends Exception {

        public NotFoundInternalGraphException() {

            super("This instance has been deserialized: the graph (and all associated methods) is not longer accessible for ontology "+terminologyCv);
        }
    }
}
