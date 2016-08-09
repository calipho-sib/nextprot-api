package com.nextprot.api.annotation.builder.statement.app.export;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Parse arguments and provides MainConfig object
 *
 * Created by fnikitin on 09/08/16.
 */
class ArgumentParser {

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
        mainConfig.setGeneListToExport(parseGeneListToExport(commandLine));

        String[] remainder = commandLine.getArgs();

        if (remainder.length != 1)
            throw new ParseException("missing output filename");

        mainConfig.setOutputFilename(remainder[0]);

        return mainConfig;
    }

    private StatementExportApp.SpringConfig parseSpringConfig(CommandLine commandLine) {

        if (commandLine.hasOption("p")) {
            System.out.print("Option profile is present.  The value is: ");
            System.out.println(commandLine.getOptionValue("p"));

            return new StatementExportApp.SpringConfig(commandLine.getOptionValue("p"));
        }

        return new StatementExportApp.SpringConfig();
    }

    private StatementExporter.Config parseExporterConfig(CommandLine commandLine) {

        if (commandLine.hasOption("c")) {
            System.out.print("Option category is present.  The value is: ");
            System.out.println(commandLine.getOptionValue("c"));

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
            System.out.print("Option gene is present.  The value is: ");
            System.out.println(commandLine.getOptionValue("g"));

            Iterable<String> genes = Splitter.on(',')
                    .trimResults()
                    .omitEmptyStrings()
                    .split(commandLine.getOptionValue("g"));

            return Lists.newArrayList(genes);
        }

        return new ArrayList<>();
    }
}
