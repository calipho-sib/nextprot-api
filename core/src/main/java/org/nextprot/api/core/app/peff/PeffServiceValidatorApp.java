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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Example of parameters:
 *
 * <ul>
 * <li>spring config profiles : -p "dev, cache"</li>
 * <li>output directory       : -o /home/npdbxref/output/ </li>
 * </ul>
 */
public class PeffServiceValidatorApp extends SpringBasedApp<PeffServiceValidatorApp.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(PeffServiceValidatorApp.class);

    private PeffServiceValidatorApp(String[] args) throws ParseException {

        super(args);
    }

    @Override
    public ArgumentParser newCommandLineParser() {

        return new ArgumentParser(PeffServiceValidatorApp.class.getSimpleName());
    }

    @Override
    protected void execute() throws IOException {

        analysingDifferences(readExpectedIsoformPeffHeaders(), getIsoformPeffHeadersFromAPI());
    }

    private void analysingDifferences(Map<String, Map<String, Object>> expected, Map<String, Map<String, Object>> observed) {

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(observed.size());
        pb.setTaskName("analysing diffs");
        pb.start();

        //pb.run(observed, (o) -> compare(expected.get(isoformAccession), o));

        for (String isoformAccession : observed.keySet()) {

            compare(expected.get(isoformAccession), observed.get(isoformAccession));
            pb.incrementValue();
        }
        pb.stop();
    }

    private void compare(Map<String, Object> expected, Map<String, Object> observed) {

        System.out.println("\n### Analysing isoform "+expected.get(IsoformSequenceInfoPeff.PEFF_KEY.DbUniqueId.getName()) + " ###");

        for (IsoformSequenceInfoPeff.PEFF_KEY peffKey : IsoformSequenceInfoPeff.PEFF_KEY.values()) {

            String key = peffKey.getName();

            Object expectedValue = expected.get(key);
            Object observedValue = observed.get(key);

            System.out.print("\tComparing value of key "+key+"... ");
            if (!Objects.equals(expectedValue, observedValue)) {
                System.out.println(" [DIFFS]\n\t\texpected="+ expectedValue+"\n\t\tobserved="+observedValue);
            }
            else {
                System.out.println(" [EQUALS]");
            }
        }
    }

    private Map<String, Map<String, Object>> readExpectedIsoformPeffHeaders() throws FileNotFoundException {

        Map<String, Map<String, Object>> map = new HashMap<>();

        String filename = "/Users/fnikitin/Documents/sib/nextprot/peff/nextprot_all_updatedTo1.0h.peff";

        ConsoleProgressBar pb = ConsoleProgressBar.indeterminated();
        pb.setTaskName("reading expected peff headers from file");
        pb.start();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith(">nxp:")) {

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
                    map.put(isoformAccession, kvm);
                    pb.incrementValue();
                }
            }
            pb.stop();

            return map;
        } catch (IOException e) {

            throw new IllegalStateException(e.getMessage()+": cannot open file nextprot_all_updatedTo1.0h.peff");
        }
    }

    private Map<String, Map<String, Object>> getIsoformPeffHeadersFromAPI() throws FileNotFoundException {

        PeffService peffService = getBean(PeffService.class);
        IsoformService isoformService = getBean(IsoformService.class);

        Set<String> allEntryAcs = getNextprotEntries();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(allEntryAcs.size());
        pb.setTaskName("querying peff headers from api");
        pb.start();

        Map<String, Map<String, Object>> map = new HashMap<>();

        for (String entryAc : allEntryAcs) {

            try {
                isoformService.findIsoformsByEntryName(entryAc).stream()
                        .map(Isoform::getIsoformAccession)
                        .forEach(isoformAccession -> map.put(isoformAccession,
                                IsoformSequenceInfoPeff.toMap(peffService.formatSequenceInfo(isoformAccession))))
                ;

                pb.incrementValue();
            } catch (EntryNotFoundException e) {

                LOGGER.error(e.getMessage() + ": skipping entry " + entryAc);
            }
        }

        pb.stop();

        return map;
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

            if (commandLine.hasOption("f")) {
                entriesFilename = commandLine.getOptionValue("f");
            }
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
