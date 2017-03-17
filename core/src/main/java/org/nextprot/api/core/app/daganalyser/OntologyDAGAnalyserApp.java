package org.nextprot.api.core.app.daganalyser;

import grph.Grph;
import grph.path.Path;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.github.jamm.MemoryMeter;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.commons.utils.app.CommandLineSpringParser;
import org.nextprot.api.commons.utils.app.SpringBasedApp;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.core.utils.graph.OntologyDAG;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This app analyses the graph of all ontologies referenced by neXtProt
 */
// -ea -javaagent:/Users/fnikitin/Projects/jamm/target/jamm-0.3.2-SNAPSHOT.jar
public class OntologyDAGAnalyserApp extends SpringBasedApp<OntologyDAGAnalyserApp.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(OntologyDAGAnalyserApp.class);

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private OntologyDAGAnalyserApp(String[] args) throws ParseException {

        super(args);
    }

    @Override
    public ArgumentParser newCommandLineParser() {

        return new ArgumentParser("dbxrefanalyser");
    }

    @Override
    protected void execute() throws IOException {

        TerminologyService terminologyService = getConfig().getBean(TerminologyService.class);

        buildTerminologyCache(terminologyService);
        Set<TerminologyCv> excludedOntology = EnumSet.of(
                TerminologyCv.NextprotCellosaurusCv, TerminologyCv.MeshAnatomyCv, TerminologyCv.MeshCv);

        PrintWriter pw = new PrintWriter(getCommandLineParser().getOutputDirectory()+"/dag-ontology.csv");

        pw.write(getStatisticsHeaders().stream().collect(Collectors.joining(",")));
        pw.write(",building time (ms),TerminologyUtils.getAllAncestors() time (ms),OntologyDAG.getAncestors() time (ms)\n");

        for (TerminologyCv terminologyCv : TerminologyCv.values()) {

            Instant t1 = Instant.now();
            OntologyDAG graph = new OntologyDAG(terminologyCv, terminologyService);
            long buildingTime = ChronoUnit.MILLIS.between(t1, Instant.now());

            try {
                List<String> statistics = calcStatistics(graph, terminologyCv);
                statistics.add(new DecimalFormat("######.##").format(buildingTime));
                if (!excludedOntology.contains(terminologyCv)) {
                    statistics.addAll(benchmarkingGetAncestorsMethods(terminologyCv, terminologyService).stream().map(l -> Long.toString(l)).collect(Collectors.toList()));
                }
                else {
                    statistics.addAll(Arrays.asList("NA", "NA"));
                }
                pw.write(statistics.stream().collect(Collectors.joining(",")));

                pw.write("\n");
                pw.flush();
            } catch (OntologyDAG.NotFoundInternalGraphException e) {

                throw new IllegalStateException(e);
            }
        }

        pw.close();
    }

    public static List<String> getStatisticsHeaders() {

        return Arrays.asList("terminology", "nodes#", "edges#", "connected components#", "cycles#",
                "avg in-degree#", "avg out-degree#", "all paths#",
                "all graph memory (KB)", "descendants map memory (KB)", "cv id->accession memory (KB)",
                "precomputing time (ms)");
    }

    private List<String> calcStatistics(OntologyDAG graph, TerminologyCv ontology) throws OntologyDAG.NotFoundInternalGraphException {

        // 1. git clone https://github.com/fnikitin/jamm.git ; cd jamm ; ant jar ; add dependency to this jar
        // 2. start the JVM with "-javaagent:<path to>/jamm.jar"
        MemoryMeter memMeter = new MemoryMeter();

        long wholeGraphMemory = memMeter.measureDeep(graph);
        long ancestorsMemory = memMeter.measureDeep(graph.getCvTermIdAncestors());
        long cvTermIdAccessionMemory = memMeter.measureDeep(graph.getCvTermIdByAccession());

        Collection<Path> allPaths = graph.getAllPathsFromTransientGraph();

        Instant t1 = Instant.now();
        for (Path path : allPaths) {

            graph.isAncestorOf(path.getSource(), path.getDestination());
        }
        long ms = ChronoUnit.MILLIS.between(t1, Instant.now());

        Set<Path> cycles = graph.getAllCyclesFromTransientGraph();

        if (!cycles.isEmpty()) {
            System.err.println("ERROR IN "+ontology + ": found "+cycles.size()+" cycles: "+cycles.stream()
                    .map(path -> Arrays.stream(path.toVertexArray())
                            .boxed()
                            .map(graph::getCvTermAccessionById)
                            .collect(Collectors.joining(" > ")))
                    .collect(Collectors.joining(", ")));
        }

        List<Number> stats = Arrays.asList(graph.countNodes(), graph.countEdgesFromTransientGraph(), graph.getConnectedComponentsFromTransientGraph().count(), cycles.size(),
                graph.getAverageDegreeFromTransientGraph(Grph.TYPE.vertex, Grph.DIRECTION.in), graph.getAverageDegreeFromTransientGraph(Grph.TYPE.vertex, Grph.DIRECTION.out), allPaths.size(),
                (wholeGraphMemory/1024.), (ancestorsMemory/1024), (cvTermIdAccessionMemory/1024), ms);

        return Stream.concat(Stream.of(graph.getTerminologyCv().name()), stats.stream().map(DECIMAL_FORMAT::format)).collect(Collectors.toList());
    }

    private void buildTerminologyCache(TerminologyService terminologyService) {

        Set<String> allCvTerms = new HashSet<>();

        // cache all (211367 terms)
        Instant t = Instant.now();
        for (TerminologyCv ontology : TerminologyCv.values()) {

            allCvTerms.addAll(terminologyService.findCvTermsByOntology(ontology.name()).stream()
                    .map(CvTerm::getAccession)
                    .collect(Collectors.toSet()));
        }
        System.out.println("access/cache "+TerminologyCv.values().length+" terminologies via terminologyService.findCvTermsByOntology: "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");

        t = Instant.now();
        allCvTerms.forEach(terminologyService::findCvTermByAccession);
        System.out.println("access/cache "+allCvTerms.size()+" terms via terminologyService.findCvTermByAccession: "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");
    }

    private List<Long> benchmarkingGetAncestorsMethods(TerminologyCv terminologyCv, TerminologyService terminologyService) {

        List<Long> timings = new ArrayList<>();

        OntologyDAG graph = new OntologyDAG(terminologyCv, terminologyService);

        Map<Long, List<String>> ancestors = new HashMap<>();
        Map<Long, long[]> ancestorsQuick = new HashMap<>();

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(terminologyCv.name());

        // COMPARE COMPUTATION DURATIONS
        Instant t = Instant.now();
        for (CvTerm cvTerm : cvTerms) {
            ancestors.put(cvTerm.getId(), TerminologyUtils.getAllAncestors(cvTerm.getAccession(), terminologyService));
        }
        timings.add(ChronoUnit.MILLIS.between(t, Instant.now()));

        t = Instant.now();

        for (CvTerm cvTerm : cvTerms) {

            ancestorsQuick.put(cvTerm.getId(), graph.getAncestors(cvTerm.getId()));
        }
        timings.add(ChronoUnit.MILLIS.between(t, Instant.now()));

        // TEST CORRECTNESS
        Set<Long> ids = ancestors.keySet();

        for (long id : ids) {

            Set<Long> ancestorsOld = ancestors.get(id).stream().map(accession -> {
                try {
                    return graph.getCvTermIdByAccession(accession);
                } catch (OntologyDAG.NotFoundNodeException e) {
                    return -1L;
                }
            }).collect(Collectors.toSet());

            Set<Long> ancestorsNew = Arrays.stream(ancestorsQuick.get(id)).boxed().collect(Collectors.toSet());

            boolean equals = ancestorsOld.equals(ancestorsNew);

            if (!equals) {

                System.err.println("WARNING: INCONSISTENCY: found different ancestors for cv term "+graph.getCvTermAccessionById(id)
                        + "\n\t: old="+ ancestors.get(id) + "\n"
                        + "\t: new="+ Arrays.stream(ancestorsQuick.get(id)).boxed()
                        .map(graph::getCvTermAccessionById)
                        .collect(Collectors.joining(", "))
                );
            }
        }

        return timings;
    }

    /**
     * Parse arguments and provides MainConfig object
     *
     * Created by fnikitin on 09/08/16.
     */
    static class ArgumentParser extends CommandLineSpringParser {

        private String outputDirectory;

        public ArgumentParser(String appName) {
            super(appName);
        }

        @Override
        protected Options createOptions() {

            Options options = super.createOptions();

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("out").hasArg().withDescription("output directory").create("o"));

            return options;
        }

        @Override
        protected void parseOtherParams(CommandLine commandLine) {

            outputDirectory = (commandLine.hasOption("o")) ? commandLine.getOptionValue("o") : "/tmp";
        }

        public String getOutputDirectory() {

            return outputDirectory;
        }
    }

    /**
     * @param args contains mandatory and optional arguments
     *  Mandatory : export-dir-path
     *  Optional  :
     *      -p profile (by default: dev, cache)
     *      -o output directory (/tmp by default)
     */
    public static void main(String[] args) {

        try {
            new OntologyDAGAnalyserApp(args).run();
        } catch(Exception e) {

            LOGGER.error(e.getMessage()+": exiting app");
            e.printStackTrace();

            System.exit(1);
        }
    }
}
