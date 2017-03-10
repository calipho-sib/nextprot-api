package org.nextprot.api.core.utils.graph;

import com.google.common.base.Preconditions;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import grph.path.Path;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import org.github.jamm.MemoryMeter;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import toools.collection.bigstuff.longset.LongCursor;
import toools.collection.bigstuff.longset.LongHashSet;
import toools.collection.bigstuff.longset.LongSet;
import toools.math.MathsUtilities;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A hierarchy of {@code CvTerm}s organised in a graph.
 *
 * Created by fnikitin on 08.03.17.
 */
public class CvTermGraph implements Serializable {

    private final static Logger LOGGER = Logger.getLogger(CvTermGraph.class.getSimpleName());
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("######.##");

    private final Grph graph;
    private final TerminologyCv terminologyCv;
    private final Map<Long, CvTerm> cvTermById;
    private final Map<String, Long> cvTermIdByAccession;
    private final Map<Long, LongSet> descendants;
    private final long memoryNoPrecomputation;
    private final int allPathsSize;

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

        memoryNoPrecomputation = computeMemory(this);
        allPathsSize = precomputeAllDescendants();

        sanityCheck();
        logSummary();
    }

    private void sanityCheck() {

        if (graph.getNumberOfVertices() != cvTermById.keySet().size()) {
            throw new IllegalStateException(terminologyCv.name() + " inconsistent graph state: invalid number of vertices");
        }
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
        sb.append(", memory (KB)=");
        reportMemoryDetailed(sb);
        sb.append("}");

        LOGGER.info(sb.toString());

        if (ccs.size() > 1) {
            LOGGER.warning(terminologyCv.name() + " has "+ccs.size()+" unconnected components");
        }
    }

    private long computeMemory(Object o) {
        // 1. git clone https://github.com/fnikitin/jamm.git ; cd jamm ; ant jar ; add dependency to this jar
        // 2. start the JVM with "-javaagent:<path to>/jamm.jar"

        return new MemoryMeter().measureDeep(o);
    }

    public void reportMemoryDetailed(StringBuilder sb) {
        // 1. git clone https://github.com/fnikitin/jamm.git ; cd jamm ; ant jar ; add dependency to this jar
        // 2. start the JVM with "-javaagent:<path to>/jamm.jar"

        MemoryMeter memMeter = new MemoryMeter();

        sb.append("[all=")
                .append(DECIMAL_FORMAT.format(memMeter.measureDeep(this) / 1024.))
                .append(", graph=").append(DECIMAL_FORMAT.format(memMeter.measureDeep(graph) / 1024.))
                .append(", cv-term-byid map=").append(DECIMAL_FORMAT.format(memMeter.measureDeep(cvTermById) / 1024.))
                .append(", cv-term-id-by-accession map=").append(DECIMAL_FORMAT.format(memMeter.measureDeep(cvTermIdByAccession) / 1024.))
                .append(", descendants map=").append(DECIMAL_FORMAT.format(memMeter.measureDeep(descendants) / 1024.))
                .append("]");
    }

    public static List<String> getStatisticsHeaders() {

        return Arrays.asList("terminology", "nodes#", "edges#", "connected components#", "avg in-degree#", "avg out-degree#", "all paths#", "memory (KB)", "precomputing memory footprint (KB)", "precomputing time (ms)");
    }

    public List<String> calcStatistics() {

        long memory = computeMemory(this);
        long precomputationFootprint = memory - memoryNoPrecomputation;

        Instant t1 = Instant.now();
        for (Path path : graph.getAllPaths()) {

            isAncestorOf(path.getSource(), path.getDestination());
        }
        long ms = ChronoUnit.MILLIS.between(t1, Instant.now());

        List<Number> stats = Arrays.asList(countNodes(), countEdges(), graph.getConnectedComponents().size(),
                getAverageDegree(Grph.TYPE.vertex, Grph.DIRECTION.in), getAverageDegree(Grph.TYPE.vertex, Grph.DIRECTION.out),
                allPathsSize, (memory/1024.), (precomputationFootprint/1024), ms);

        return Stream.concat(Stream.of(terminologyCv.name()), stats.stream().map(n -> DECIMAL_FORMAT.format(n))).collect(Collectors.toList());
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

    public Map<String, CvTerm> exportMap() {

        Map<String, CvTerm> map = new HashMap<>();

        for (CvTerm cvTerm : cvTermById.values()) {

            map.put(cvTerm.getAccession(), cvTerm);
        }

        return map;
    }

    /**
     * @return all the paths of this graph
     */
    public Collection<Path> getAllPaths() {
        return graph.getAllPaths();
    }

    public class NotFoundNodeException extends Exception {

        public NotFoundNodeException(String accession) {

            super("CvTerm node with accession "+accession+" was not found in "+terminologyCv + " graph");
        }
    }

    // There is a deprecation in toools.math.MathsUtilities; grph.getAverageDegree() should call another method like below
    double getAverageDegree(Grph.TYPE type, Grph.DIRECTION direction)  {

        LongArrayList l = new LongArrayList();

        for (LongCursor c : graph.getVertices())
        {
            l.add(graph.getVertexDegree(c.value, type, direction));
        }

        return MathsUtilities.computeAverage(l.toLongArray());
    }
}
