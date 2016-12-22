package org.nextprot.api.tasks.dbxref;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.exception.EntryNotFoundException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.tasks.utils.CommandLineSpringParser;
import org.nextprot.api.tasks.utils.ConsoleProgressBar;
import org.nextprot.api.tasks.utils.SpringBasedApp;

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
public class DbXrefVisitorApp extends SpringBasedApp<DbXrefVisitorApp.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(DbXrefVisitorApp.class);

    private final String outputDirectory;

    private DbXrefVisitorApp(String[] args) throws ParseException {

        super(args);
        outputDirectory = getCommandLineParser().getOutputDirectory();
    }

    @Override
    public ArgumentParser newCommandLineParser() {

        return new ArgumentParser();
    }

    @Override
    protected void execute() throws IOException {

        visitAllEntryDbXrefs();
        visitAllTerminologyDbXrefs();
    }

    private void visitAllEntryDbXrefs() throws IOException {

        DbXrefUrlVisitor visitor = new DbXrefUrlVisitor(outputDirectory + "/allentries-xrefs-url.tsv",
                outputDirectory + "/allentries-xrefs-url.log");

        DbXrefService xrefService = getConfig().getBean(DbXrefService.class);

        Set<String> allEntryAcs = getNextprotEntries();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(allEntryAcs.size());
        pb.start();

        int i=0;
        for (String entryAc : allEntryAcs) {

            try {
                visitor.visit(entryAc, xrefService.findDbXrefsByMaster(entryAc));
                visitor.flush();
                pb.setValue(++i);
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

    private void visitAllTerminologyDbXrefs() throws IOException {

        DbXrefUrlVisitor visitor = new DbXrefUrlVisitor(outputDirectory + "/allterminologies-xrefs-url.tsv",
                outputDirectory + "/allterminologies-xrefs-url.log");

        TerminologyService terminologyService = getConfig().getBean(TerminologyService.class);

        List<CvTerm> allCvTerms = terminologyService.findAllCVTerms();

        ConsoleProgressBar pb = ConsoleProgressBar.determinated(allCvTerms.size());

        pb.start();

        int i=0;
        for (CvTerm terminology : allCvTerms) {

            visitor.visit(terminology.getAccession(), terminology.getXrefs());
            visitor.flush();

            pb.setValue(++i);
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

        @Override
        protected Options createOptions() {

            Options options = super.createOptions();

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("out").hasArg().withDescription("output directory").create("o"));

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription("input file containing entries to visit").create("f"));

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
            new DbXrefVisitorApp(args).run();
        } catch(Exception e) {

            LOGGER.error(e.getMessage());
            LOGGER.info("exiting app");
            System.exit(1);
        }
    }
}
