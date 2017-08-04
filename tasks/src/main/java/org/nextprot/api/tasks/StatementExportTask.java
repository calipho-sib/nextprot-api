package org.nextprot.api.tasks;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.nextprot.api.annotation.builder.statement.StatementExporter;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.app.CommandLineSpringParser;
import org.nextprot.api.commons.utils.app.SpringBasedTask;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This application exports specified genes statement as tab-delimited files.
 * It requires one single argument output file path with optional tsv extension.
 *
 * Example of parameters:
 *
 * <ul>
 * <li>no options             : /tmp/allgenes</li>
 * <li>all genes with options : -p "dev, cache" -c "mutagenesis site" /tmp/allgenes</li>
 * <li>specific category      : -c "mutagenesis site" -g "brca1, scn2a" /tmp/two-genes.tsv</li>
 * </ul>
 *
 * Created by fnikitin on 09/08/16.
 */
public class StatementExportTask extends SpringBasedTask<StatementExportTask.ArgumentParser> {

    private static final Logger LOGGER = Logger.getLogger(StatementExportTask.class);

    private StatementExportTask(String[] args) throws ParseException {

        super(args);
    }

    @Override
    protected ArgumentParser newCommandLineParser() {
        return new ArgumentParser("statementexporter");
    }

    @Override
    protected void execute() throws FileNotFoundException {

        StatementDao statementDao = getBean(StatementDao.class);
        MasterIdentifierService masterIdentifierService = getBean(MasterIdentifierService.class);

        ArgumentParser parser = getCommandLineParser();
        StatementExporter exporter = new StatementExporter(statementDao, masterIdentifierService, parser.getExporterConfig());

        LOGGER.info("exporting gene statements...");

        // get all genes if no genes were specified
        if (parser.getSpecificGeneListToExport().isEmpty()) {

            Map<String, String> map = exporter.exportAllGeneStatementsAsTsvString();
            for (String geneName : map.keySet()) {

                exporter.exportAsTsvFile(parser.getOutputDirname(), geneName, map.get(geneName));
                LOGGER.info("gene statements of " + geneName + " exported");
            }
        }
        else {
            for (String geneName : parser.getSpecificGeneListToExport()) {

                exporter.exportAsTsvFile(parser.getOutputDirname(), geneName, exporter.exportGeneStatementsAsTsvString(geneName));
                LOGGER.info("gene statements of " + geneName + " exported");
            }
        }
    }

    /**
     * Parse arguments and provides MainConfig object
     *
     * Created by fnikitin on 09/08/16.
     */
    static class ArgumentParser extends CommandLineSpringParser {

        private StatementExporter.Config exporterConfig;
        private List<String> specificGeneListToExport;
        private String outputDirname;

        public ArgumentParser(String appName) {
            super(appName);
        }

        @Override
        protected Options createOptions() {

            Options options = super.createOptions();

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("categories").hasArg().withDescription("filtered categories (default: variant, mutagenesis)").create("c"));
            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("genes").hasArg().withDescription("genes to export (all genes are exported if not defined)").create("g"));

            return options;
        }

        @Override
        protected void parseOtherParams(CommandLine commandLine) throws ParseException {

            exporterConfig = parseExporterConfig(commandLine);
            specificGeneListToExport = parseGeneListToExport(commandLine);

            String[] remainder = commandLine.getArgs();

            if (remainder.length != 1)
                throw new ParseException("missing output directory");

            outputDirname = remainder[0];
        }

        private StatementExporter.Config parseExporterConfig(CommandLine commandLine) {

            if (commandLine.hasOption("c")) {
                Iterable<String> categories = Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .split(commandLine.getOptionValue("c"));

                return new StatementExporter.Config(Iterables.toArray(categories, String.class));
            }

            return new StatementExporter.Config();
        }

        private List<String> parseGeneListToExport(CommandLine commandLine) {

            if (commandLine.hasOption("g")) {
                Iterable<String> genes = Splitter.on(',')
                        .trimResults()
                        .omitEmptyStrings()
                        .split(commandLine.getOptionValue("g"));

                return Lists.newArrayList(genes);
            }

            return new ArrayList<>();
        }

        StatementExporter.Config getExporterConfig() {
            return exporterConfig;
        }

        List<String> getSpecificGeneListToExport() {
            return specificGeneListToExport;
        }

        String getOutputDirname() {
            return outputDirname;
        }

        @Override
        public String toString() {
            return  "Parameters\n" +
                    " - exporterConfig   : " + exporterConfig + "\n" +
                    " - specificGeneListToExport : " + specificGeneListToExport + "\n" +
                    " - outputDirname    : '" + outputDirname + '\'';
        }
    }

    /**
     * @param args contains mandatory and optional arguments
     *  Mandatory : export-dir-path
     *  Optional  :
     *      -p profile (by default: dev, cache)
     *      -c filtered-categories (by default: variant, mutagenesis)
     *      -g genes (by default all genes statements are exported)
     */
    public static void main(String[] args) throws Exception {

        new StatementExportTask(args).run();
    }
}
