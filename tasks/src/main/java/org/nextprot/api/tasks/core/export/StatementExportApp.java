package org.nextprot.api.tasks.core.export;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.nextprot.api.annotation.builder.statement.StatementExporter;
import com.nextprot.api.annotation.builder.statement.dao.StatementDao;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.tasks.utils.SpringApp;
import org.nextprot.api.tasks.utils.SpringConfig;

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
public class StatementExportApp implements SpringApp {

    private static final Logger LOGGER = Logger.getLogger(StatementExportApp.class);

    private final MainConfig config;

    private StatementExportApp(String[] args) throws ParseException {

        this.config = new ArgumentParser(args).getConfig();
        LOGGER.info(config);
    }

    /**
     * @param args contains mandatory and optional arguments
     *  Mandatory : export-dir-path
     *  Optional  :
     *      -p profile (by default: dev, cache)
     *      -c filtered-categories (by default: variant, mutagenesis)
     *      -g genes (by default all genes statements are exported)
     */
    public static void main(String[] args) throws ParseException, FileNotFoundException {

        StatementExportApp app = new StatementExportApp(args);

        app.start();
        app.exportGenesStatements();
        app.stop();
    }

    private void exportGenesStatements() throws FileNotFoundException {

        StatementDao statementDao = config.getSpringConfig().getBean(StatementDao.class);
        MasterIdentifierService masterIdentifierService = config.getSpringConfig().getBean(MasterIdentifierService.class);

        StatementExporter exporter = new StatementExporter(statementDao, masterIdentifierService, config.getExporterConfig());

        LOGGER.info("exporting gene statements...");

        // get all genes if no genes were specified
        if (config.getSpecificGeneListToExport().isEmpty()) {

            Map<String, String> map = exporter.exportAllGeneStatementsAsTsvString();
            for (String geneName : map.keySet()) {

                exporter.exportAsTsvFile(config.getOutputDirname(), geneName, map.get(geneName));
                LOGGER.info("gene statements of " + geneName + " exported");
            }
        }
        else {
            for (String geneName : config.getSpecificGeneListToExport()) {

                exporter.exportAsTsvFile(config.getOutputDirname(), geneName, exporter.exportGeneStatementsAsTsvString(geneName));
                LOGGER.info("gene statements of " + geneName + " exported");
            }
        }
    }

    @Override
    public SpringConfig getSpringConfig() {

        return config.getSpringConfig();
    }

    @Override
    public void start() {

        LOGGER.info("starting spring application context...");
        config.getSpringConfig().startApplicationContext();
        LOGGER.info("spring application context started");
    }

    @Override
    public void stop() {
        LOGGER.info("closing spring application context...");
        config.getSpringConfig().stopApplicationContext();
        LOGGER.info("spring application context closed");
    }

    static class MainConfig {

        private SpringConfig springConfig;
        private StatementExporter.Config exporterConfig;
        private List<String> specificGeneListToExport;
        private String outputDirname;

        SpringConfig getSpringConfig() {
            return springConfig;
        }

        void setSpringConfig(SpringConfig springConfig) {
            this.springConfig = springConfig;
        }

        StatementExporter.Config getExporterConfig() {
            return exporterConfig;
        }

        void setExporterConfig(StatementExporter.Config exporterConfig) {
            this.exporterConfig = exporterConfig;
        }

        List<String> getSpecificGeneListToExport() {
            return specificGeneListToExport;
        }

        void setSpecificGeneListToExport(List<String> specificGeneListToExport) {
            this.specificGeneListToExport = specificGeneListToExport;
        }

        String getOutputDirname() {
            return outputDirname;
        }

        void setOutputDirname(String outputDirname) {
            this.outputDirname = outputDirname;
        }

        @Override
        public String toString() {
            return  "Parameters\n" +
                    " - springConfig     : " + springConfig + "\n" +
                    " - exporterConfig   : " + exporterConfig + "\n" +
                    " - specificGeneListToExport : " + specificGeneListToExport + "\n" +
                    " - outputDirname    : '" + outputDirname + '\'';
        }
    }

    /**
     * Parse arguments and provides MainConfig object
     *
     * Created by fnikitin on 09/08/16.
     */
    static class ArgumentParser {

        private final StatementExportApp.MainConfig config;

        ArgumentParser(String[] args) throws ParseException {

            this.config = parseArgs(args);
        }

        public StatementExportApp.MainConfig getConfig() {
            return config;
        }

        private Options createOptions() {

            Options options = new Options();

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("profile").hasArg().withDescription("spring profile (default: dev, cache)").create("p"));
            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("categories").hasArg().withDescription("filtered categories (default: variant, mutagenesis)").create("c"));
            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("genes").hasArg().withDescription("genes to export (all genes are exported if not defined)").create("g"));

            return options;
        }

        private StatementExportApp.MainConfig parseArgs(String[] args) throws ParseException {

            Options options = createOptions();

            CommandLineParser parser = new GnuParser();

            CommandLine commandLine = parser.parse(options, args);

            StatementExportApp.MainConfig mainConfig = new StatementExportApp.MainConfig();

            mainConfig.setSpringConfig(parseSpringConfig(commandLine));
            mainConfig.setExporterConfig(parseExporterConfig(commandLine));
            mainConfig.setSpecificGeneListToExport(parseGeneListToExport(commandLine));

            String[] remainder = commandLine.getArgs();

            if (remainder.length != 1)
                throw new ParseException("missing output directory");

            mainConfig.setOutputDirname(remainder[0]);

            return mainConfig;
        }

        private SpringConfig parseSpringConfig(CommandLine commandLine) {

            if (commandLine.hasOption("p")) {
                return new SpringConfig(commandLine.getOptionValue("p"));
            }

            return new SpringConfig();
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
    }
}
