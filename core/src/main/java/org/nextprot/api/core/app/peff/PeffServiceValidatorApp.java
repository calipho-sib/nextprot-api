package org.nextprot.api.core.app.peff;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.app.CommandLineSpringParser;
import org.nextprot.api.commons.utils.app.ConsoleProgressBar;
import org.nextprot.api.commons.utils.app.SpringBasedApp;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformSequenceInfoPeff;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.PeffService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Example of parameters:
 *
 * -p "dev, cache"
 * -o /home/fnikitin/Projects/resources/peff
 * -f /home/fnikitin/Projects/nextprot-api/core/src/main/resources/org/nextprot/api/core/app/peff/few-entries.txt
 * /home/fnikitin/Projects/resources/peff/nextprot_all_updatedTo1.0h.peff
 *
 * <ul>
 * <li>spring config profiles : -p "dev, cache"</li>
 * <li>output directory       : -o /home/fnikitin/Projects/resources/peff </li>
 * </ul>
 */
public class PeffServiceValidatorApp extends SpringBasedApp<PeffServiceValidatorApp.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(PeffServiceValidatorApp.class);

    private final String expectedPeffFilename;
    private final String outputDirectory;

    private PeffServiceValidatorApp(String[] args) throws ParseException {

        super(args);

        if (!getCommandLineParser().hasRemainingArguments()) {
            throw new IllegalArgumentException("missing file nextprot_all_updatedTo1.0h.peff");
        }
        expectedPeffFilename = getCommandLineParser().getRemainingArguments().get(0);
        outputDirectory = getCommandLineParser().getOutputDirectory();
    }

    @Override
    public ArgumentParser newCommandLineParser() {

        return new ArgumentParser(PeffServiceValidatorApp.class.getSimpleName());
    }

    @Override
    protected void execute() throws IOException {

        String output = analysingDifferences(readExpectedIsoformPeffHeaders(), getIsoformPeffHeadersFromAPI());

        PrintWriter pw = new PrintWriter(outputDirectory + "/peff-diffs.txt");
        pw.write(output);
        pw.close();
    }

    private String analysingDifferences(Map<String, Map<String, Object>> expected, Map<String, Map<String, Object>> observed) {

        ConsoleProgressBar pb = ConsoleProgressBar.determinated("analysing diffs", observed.size() );
        DataToCompareConsumer consumer = new DataToCompareConsumer(expected, observed);
        pb.run(observed.keySet().stream(), consumer);

        return consumer.getOutput();
    }

    private Map<String, Map<String, Object>> readExpectedIsoformPeffHeaders() throws FileNotFoundException {

        ConsoleProgressBar pb = ConsoleProgressBar.indeterminated("reading expected peff headers from file");

        try {
            ExpectedPeffParser parser = new ExpectedPeffParser();
            pb.run(Files.lines(Paths.get(expectedPeffFilename)).filter(l -> l.startsWith(">nxp:")), parser);

            return parser.isoToPeffHeader;
        } catch (IOException e) {

            throw new IllegalStateException(e.getMessage()+": cannot open file nextprot_all_updatedTo1.0h.peff");
        }
    }

    private Map<String, Map<String, Object>> getIsoformPeffHeadersFromAPI() throws FileNotFoundException {

        Set<String> allEntryAcs = getNextprotEntries();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated("querying peff headers from api", allEntryAcs.size());
        ObservedPeffCollector collector = new ObservedPeffCollector(getBean(PeffService.class), getBean(IsoformService.class));
        pb.run(allEntryAcs.stream(), collector);

        return collector.map;
    }

    private Set<String> getNextprotEntries() throws FileNotFoundException {

        Set<String> entries = getCommandLineParser().getNextprotEntriesFromFile();

        if (entries.isEmpty()) {
            return getBean(MasterIdentifierService.class).findUniqueNames();
        }
        return entries;
    }

    /**
     * Parse arguments and provides MainConfig object
     *
     * Created by fnikitin on 09/08/16.
     */
    static class ArgumentParser extends CommandLineSpringParser {

        private String outputDirectory;
        private String entriesFilename;

        ArgumentParser(String appName) {
            super(appName);
        }

        @Override
        protected Options createOptions() {

            Options options = super.createOptions();

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("out").hasArg().withDescription("output directory").create("o"));

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("input file containing neXtProt entry accessions to analyse").create("f"));

            return options;
        }

        @Override
        protected void parseOtherParams(CommandLine commandLine) {

            outputDirectory = (commandLine.hasOption("o")) ? commandLine.getOptionValue("o") : "./";

            if (commandLine.hasOption("f")) {
                entriesFilename = commandLine.getOptionValue("f");
            }
        }

        public String getOutputDirectory() {

            return outputDirectory;
        }

        Set<String> getNextprotEntriesFromFile() throws FileNotFoundException {

            if (entriesFilename != null) {

                BufferedReader fr = new BufferedReader(new FileReader(entriesFilename));

                return fr.lines()
                        .filter(l -> !l.isEmpty())
                        .filter(l -> !l.startsWith("#"))
                        .collect(Collectors.toSet());
            }
            return new HashSet<>();
        }
    }

    static class DataToCompareConsumer implements Consumer<String> {

        private final Map<String, Map<String, Object>> expected;
        private final Map<String, Map<String, Object>> observed;
        private final StringBuilder sb = new StringBuilder();

        DataToCompareConsumer(Map<String, Map<String, Object>> expected, Map<String, Map<String, Object>> observed) {
            this.expected = expected;
            this.observed = observed;
        }

        @Override
        public void accept(String isoformAccession) {

            compare(expected.get(isoformAccession), observed.get(isoformAccession));
        }

        private void compare(Map<String, Object> expected, Map<String, Object> observed) {

            sb
                    .append("\n### Analysing isoform ")
                    .append(expected.get(IsoformSequenceInfoPeff.PEFF_KEY.DbUniqueId.getName()))
                    .append(" ###\n");

            for (IsoformSequenceInfoPeff.PEFF_KEY peffKey : IsoformSequenceInfoPeff.PEFF_KEY.values()) {

                String key = peffKey.getName();

                Object expectedValue = expected.get(key);
                Object observedValue = observed.get(key);

                sb
                        .append("\tComparing value of key ")
                        .append(key)
                        .append("... ");

                if (!Objects.equals(expectedValue, observedValue)) {
                    sb
                            .append(" [DIFFS]\n\t\texpected=")
                            .append(expectedValue)
                            .append("\n\t\tobserved=")
                            .append(observedValue)
                            .append("\n");
                }
                else {
                    sb.append(" [EQUALS]\n");
                }
            }
        }

        public String getOutput() {
            return sb.toString();
        }
    }

    static class ExpectedPeffParser implements Consumer<String> {

        private final Map<String, Map<String, Object>> isoToPeffHeader = new HashMap<>();

        @Override
        public void accept(String line) {

            // extract isoform accession
            String[] kvs = line.split("\\\\");
            String isoformAccession = kvs[0].split(":")[1].trim();

            Map<String, Object> kvm = new HashMap<>();

            // populating peff key values
            Arrays.stream(Arrays.copyOfRange(kvs, 1, kvs.length))
                    .forEach(kvStr ->  {
                        String[] kv = kvStr.trim().split("=");

                        if (kv.length == 2) {
                            kvm.put("\\" + kv[0], IsoformSequenceInfoPeff.valueToObject(IsoformSequenceInfoPeff.PEFF_KEY.valueOf(kv[0]), kv[1]));
                        }
                    });

            // adding kvs for isoform accession
            isoToPeffHeader.put(isoformAccession, kvm);
        }
    }

    static class ObservedPeffCollector implements Consumer<String> {

        private final Map<String, Map<String, Object>> map = new HashMap<>();
        private final PeffService peffService;
        private final IsoformService isoformService;

        ObservedPeffCollector(PeffService peffService, IsoformService isoformService) {
            this.peffService = peffService;
            this.isoformService = isoformService;
        }

        @Override
        public void accept(String entryAccession) {

            try {
                isoformService.findIsoformsByEntryName(entryAccession).stream()
                        .map(Isoform::getIsoformAccession)
                        .forEach(isoformAccession -> map.put(isoformAccession,
                                IsoformSequenceInfoPeff.toMap(peffService.formatSequenceInfo(isoformAccession)))
                        )
                ;
            } catch (EntryNotFoundException e) {

                LOGGER.error(e.getMessage() + ": skipping entry " + entryAccession);
            }
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
            new PeffServiceValidatorApp(args).run();
        } catch(Exception e) {

            LOGGER.error(e.getMessage()+": exiting app");
            e.printStackTrace();

            System.exit(1);
        }
    }
}
