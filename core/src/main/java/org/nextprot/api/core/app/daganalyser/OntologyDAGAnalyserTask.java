package org.nextprot.api.core.app.daganalyser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.app.CommandLineSpringParser;
import org.nextprot.api.commons.app.ConsoleProgressBar;
import org.nextprot.api.commons.app.SpringBasedTask;
import org.nextprot.api.commons.constants.TerminologyCv;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.CvTermGraph;
import org.nextprot.api.core.service.CvTermGraphService;
import org.nextprot.api.core.service.TerminologyService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This app analyses the graph of all ontologies referenced by neXtProt
 *
 * <h3>About estimating Java Object Sizes with Instrumentation</h3>
 * Setting jamm as -javaagent is now optional
 * If instrumentation is available, use it, otherwise guess the size using sun.misc.Unsafe; if that is unavailable,
 * guess using predefined specifications
 * <pre>-javaagent: $path/jamm/target/jamm-0.3.2-SNAPSHOT.jar</pre>
 */
public class OntologyDAGAnalyserTask extends SpringBasedTask<OntologyDAGAnalyserTask.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(OntologyDAGAnalyserTask.class);

    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private TerminologyService terminologyService;
    private CvTermGraphService cvTermGraphService;
    private TerminologyCv[] terminologyCvs;

    private OntologyDAGAnalyserTask(String[] args) throws ParseException {

        super(args);
        terminologyCvs = TerminologyCv.values();
        terminologyCvs = new TerminologyCv[] {TerminologyCv.NextprotCellosaurusCv};
    }

    @Override
    public ArgumentParser newCommandLineParser() {

        return new ArgumentParser(OntologyDAGAnalyserTask.class.getSimpleName());
    }

    @Override
    protected void execute() throws IOException {

        terminologyService = getBean(TerminologyService.class);
        cvTermGraphService = getBean(CvTermGraphService.class);

        System.out.println("*** write to cache timings...");
        readWriteCache(false);
        System.out.println("*** access to cache timings...");
        readWriteCache(true);
        System.out.println("*** calculate statistics...");
        calcStatisticsForAllOntologies();
    }

    private void calcStatisticsForAllOntologies() throws FileNotFoundException {

        Set<TerminologyCv> excludedOntology = EnumSet.of(
                TerminologyCv.NextprotCellosaurusCv, TerminologyCv.MeshAnatomyCv, TerminologyCv.MeshCv);

        PrintWriter pw = new PrintWriter(getCommandLineParser().getOutputDirectory() + "/dag-ontology.csv");

        pw.write(getStatisticsHeaders().stream().collect(Collectors.joining(",")));
        pw.write(",building time (ms),TerminologyUtils.getAllAncestors() time (ms),OntologyDAG.getAncestors() time (ms)\n");

        for (TerminologyCv terminologyCv : terminologyCvs) {

            Instant t1 = Instant.now();
            // no cache here: create a new instance to access graph advanced methods
            CvTermGraph graph = cvTermGraphService.findCvTermGraph(terminologyCv);
            long buildingTime = ChronoUnit.MILLIS.between(t1, Instant.now());

            List<String> statistics = calcStatistics(graph);
            statistics.add(new DecimalFormat("######.##").format(buildingTime));
            if (!excludedOntology.contains(terminologyCv)) {
                statistics.addAll(benchmarkingGetAncestorsMethods(terminologyCv, terminologyService).stream().map(l -> Long.toString(l)).collect(Collectors.toList()));
            } else {
                statistics.addAll(Arrays.asList("NA", "NA"));
            }
            pw.write(statistics.stream().collect(Collectors.joining(",")));

            pw.write("\n");
            pw.flush();
        }

        pw.close();
    }

    private static List<String> getStatisticsHeaders() {

        return Arrays.asList("terminology", "nodes#", "edges#", "connected components#", "cycles#",
                "avg in-degree#", "avg out-degree#", "all paths#",
                "graph memory (KB)", "cv id to ancestors id memory (KB)", "cv id to accession memory (KB)",
                "precomputing time (ms)");
    }

    private List<String> calcStatistics(CvTermGraph graph) {

        /*
        // 1. git clone https://github.com/jbellis/jamm.git <path to>/ ; cd <path to>/jamm ; ant jar ; add dependency to this jar
        // 2. start the JVM with "-javaagent:<path to>/jamm.jar"
        MemoryMeter memMeter = new MemoryMeter().withGuessing(MemoryMeter.Guess.FALLBACK_BEST);

        long wholeGraphMemory = memMeter.measureDeep(graph);
        long ancestorsMemory = memMeter.measureDeep(graph.getCvTermIdAncestors());
        long cvTermIdAccessionMemory = memMeter.measureDeep(graph.getCvTermIdByAccession());

        Collection<Path> allPaths = graph.getAllPathsFromTransientGraph();

        Instant t1 = Instant.now();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(allPaths.size());
        pb.setTaskName(ontology+ " paths");
        pb.start();
        for (Path path : allPaths) {

            graph.isAncestorOf(path.getSource(), path.getDestination());
            pb.incrementValue();
        }
        pb.stop();
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

        List<Number> stats = Arrays.asList(
                graph.countNodes(),
                graph.countEdgesFromTransientGraph(),
                graph.getConnectedComponentsFromTransientGraph().count(),
                cycles.size(),
                graph.getAverageDegreeFromTransientGraph(Grph.TYPE.vertex, Grph.DIRECTION.in),
                graph.getAverageDegreeFromTransientGraph(Grph.TYPE.vertex, Grph.DIRECTION.out),
                allPaths.size(),
                (int)Math.ceil(wholeGraphMemory/1024.),
                (int)Math.ceil(ancestorsMemory/1024.),
                (int)Math.ceil(cvTermIdAccessionMemory/1024.),
                ms);

        return Stream.concat(Stream.of(graph.getTerminologyCv().name()), stats.stream().map(DECIMAL_FORMAT::format)).collect(Collectors.toList());
    */
        return new ArrayList<>();
    }

    private void readWriteCache(boolean readCacheForSure) {

        Set<String> allCvTerms = new HashSet<>();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(((readCacheForSure) ? "read":"read/write")+" terminology-by-ontology cache", terminologyCvs.length);
        pb.start();
        Instant t = Instant.now();
        for (TerminologyCv ontology : terminologyCvs) {

            allCvTerms.addAll(terminologyService.findCvTermsByOntology(ontology.name()).stream()
                    .map(CvTerm::getAccession)
                    .collect(Collectors.toSet()));
            pb.incrementValue();
        }
        pb.stop();
        System.out.println("\ttiming 'terminology-by-ontology': "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");

        pb = ConsoleProgressBar.determinated(((readCacheForSure) ? "read":"read/write")+" 'ontology-dag' cache", terminologyCvs.length);
        pb.start();
        t = Instant.now();
        for (TerminologyCv ontology : terminologyCvs) {

            cvTermGraphService.findCvTermGraph(ontology);
            pb.incrementValue();
        }
        pb.stop();
        System.out.println("\ttiming 'ontology-dag': "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");

        pb = ConsoleProgressBar.determinated(((readCacheForSure) ? "read":"read/write")+" 'terminology-by-accession' cache", allCvTerms.size());
        pb.start();
        t = Instant.now();
        for (String cvTerm : allCvTerms) {

            terminologyService.findCvTermByAccessionOrThrowRuntimeException(cvTerm);
            pb.incrementValue();
        }
        pb.stop();
        System.out.println("\ttiming 'terminology-by-accession': "+ChronoUnit.SECONDS.between(t, Instant.now()) + " s");
    }

    private List<Long> benchmarkingGetAncestorsMethods(TerminologyCv terminologyCv, TerminologyService terminologyService) {

        List<Long> timings = new ArrayList<>();

        CvTermGraph graph = cvTermGraphService.findCvTermGraph(terminologyCv);

        Map<Long, List<String>> ancestors = new HashMap<>();
        Map<Long, List<String>> ancestorsQuick = new HashMap<>();

        List<CvTerm> cvTerms = terminologyService.findCvTermsByOntology(terminologyCv.name());

        // COMPARE COMPUTATION DURATIONS
        Instant t = Instant.now();
        for (CvTerm cvTerm : cvTerms) {
            ancestors.put(cvTerm.getId(), terminologyService.getAllAncestorsAccession(cvTerm.getAccession()));
        }
        timings.add(ChronoUnit.MILLIS.between(t, Instant.now()));

        t = Instant.now();

        for (CvTerm cvTerm : cvTerms) {

            ancestorsQuick.put(cvTerm.getId(), Arrays.stream(graph.getAncestors(Math.toIntExact(cvTerm.getId()))).boxed()
                    .map(graph::getCvTermAccessionById)
                    .collect(Collectors.toList()));
        }
        timings.add(ChronoUnit.MILLIS.between(t, Instant.now()));

        // TEST CORRECTNESS
        Set<Long> ids = ancestors.keySet();

        for (long id : ids) {

            Set<String> ancestorsOld = new HashSet<>(ancestors.get(id));
            Set<String> ancestorsNew = new HashSet<>(ancestorsQuick.get(id));

            boolean equals = ancestorsOld.equals(ancestorsNew);

            if (!equals) {

                System.err.println("WARNING: INCONSISTENCY: found different ancestors for cv term "+graph.getCvTermAccessionById((int) id)
                        + "\n\t: old="+ ancestors.get(id) + "\n"
                        + "\t: new="+ ancestorsNew);
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

            outputDirectory = (commandLine.hasOption("o")) ? commandLine.getOptionValue("o") : "./";
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
            new OntologyDAGAnalyserTask(args).run();
        } catch(Exception e) {

            LOGGER.error(e.getMessage()+": exiting app");
            e.printStackTrace();

            System.exit(1);
        }
    }
}
