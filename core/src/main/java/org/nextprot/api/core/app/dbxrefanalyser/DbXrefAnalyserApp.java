package org.nextprot.api.core.app.dbxrefanalyser;

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
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.TerminologyService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Visit all dbxrefs and report http status codes
 *
 * Example of parameters:
 *
 * <ul>
 * <li>neXtProt entries file  : -f /Users/fnikitin/Projects/nextprot-api/tasks/src/main/resources/org/nextprot/api/tasks/dbxref/pam-entries.txt</li>
 * <li>spring config profiles : -p "dev, cache"</li>
 * <li>output directory       : -o /home/npdbxref/output/ </li>
 * </ul>
 *
 * Created by fnikitin on 09/08/16.
 */
public class DbXrefAnalyserApp extends SpringBasedApp<DbXrefAnalyserApp.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(DbXrefAnalyserApp.class);

    private final String outputDirectory;

    private DbXrefAnalyserApp(String[] args) throws ParseException {

        super(args);
        outputDirectory = getCommandLineParser().getOutputDirectory();
    }

    @Override
    public ArgumentParser newCommandLineParser() {

        return new ArgumentParser("dbxrefanalyser");
    }

    @Override
    protected void execute() throws IOException {

        analyseNextprotEntriesDbXrefs();
        analyseCvTermsDbXrefs();
    }

    private void analyseNextprotEntriesDbXrefs() throws IOException {

        LOGGER.info("**** Finding dbxrefs from entry accession...");

        DbXrefUrlVisitor visitor = new DbXrefUrlVisitor(outputDirectory + "/allentries-xrefs-url.tsv",
                outputDirectory + "/allentries-xrefs-url.log");

        DbXrefService xrefService = getConfig().getBean(DbXrefService.class);

        Set<String> allEntryAcs = getNextprotEntries();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(allEntryAcs.size());
        pb.setTaskName("visiting nextprot entries");
        pb.start();

        for (String entryAc : allEntryAcs) {

            try {
                visitor.visit(entryAc, xrefService.findDbXrefsByMaster(entryAc));
                visitor.flush();
                pb.incrementValue();
            } catch (EntryNotFoundException e) {

                LOGGER.error(e.getMessage()+": skipping entry "+entryAc);
            }
        }

        visitor.flush();
        visitor.close();

        pb.stop();
    }

    private Set<String> getNextprotEntries() throws FileNotFoundException {

        if (getCommandLineParser().getEntriesFilename() != null) {

            BufferedReader fr = new BufferedReader(new FileReader(getCommandLineParser().getEntriesFilename()));

            return fr.lines()
                    .filter(l -> !l.isEmpty())
                    .filter(l -> !l.startsWith("#"))
                    .collect(Collectors.toSet());
        }
        else {
            return getConfig().getBean(MasterIdentifierService.class).findUniqueNames();
        }
    }

    private void analyseCvTermsDbXrefs() throws IOException {

        LOGGER.info("**** Finding dbxrefs from terminology...");

        DbXrefUrlVisitor visitor = new DbXrefUrlVisitor(outputDirectory + "/allterminologies-xrefs-url.tsv",
                outputDirectory + "/allterminologies-xrefs-url.log");

        TerminologyService terminologyService = getConfig().getBean(TerminologyService.class);

        List<CvTerm> allCvTerms = terminologyService.findAllCVTerms();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(allCvTerms.size());
        pb.setTaskName("visiting all cv terms");

        pb.start();

        for (CvTerm terminology : allCvTerms) {

            visitor.visit(terminology.getAccession(), terminology.getXrefs());
            visitor.flush();

            pb.incrementValue();
        }

        visitor.flush();
        visitor.close();

        pb.stop();
    }

    /**
     * Parse arguments and provides MainConfig object
     *
     * Created by fnikitin on 09/08/16.
     */
    static class ArgumentParser extends CommandLineSpringParser {

        private String outputDirectory;
        private String entriesFilename;

        public ArgumentParser(String appName) {
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

            outputDirectory = (commandLine.hasOption("o")) ? commandLine.getOptionValue("o") : "/tmp";

            if (commandLine.hasOption("f")) {
                entriesFilename = commandLine.getOptionValue("f");
            }
        }

        public String getOutputDirectory() {

            return outputDirectory;
        }

        public String getEntriesFilename() {
            return entriesFilename;
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
            new DbXrefAnalyserApp(args).run();
        } catch(Exception e) {

            LOGGER.error(e.getMessage()+": exiting app");
            e.printStackTrace();

            System.exit(1);
        }
    }
}
