package org.nextprot.api.tasks.utils;

import org.apache.commons.cli.*;


public abstract class CommandLineSpringParser {

    /**
     * Parse spring specific args and put left-over non-recognized options and arguments into collector
     * @param args arguments to parse
     * @return SpringConfig
     * @throws ParseException
     */
    public SpringConfig parseSpringConfig(String[] args) throws ParseException {

        Options options = createOptions();

        CommandLineParser parser = new GnuParser();

        CommandLine commandLine = parser.parse(options, args);

        SpringConfig springConfig = (commandLine.hasOption("p")) ?
                new SpringConfig(commandLine.getOptionValue("p")) : new SpringConfig();

        parseOtherParams(commandLine);

        return springConfig;
    }

    protected Options createOptions() {

        Options options = new Options();

        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder.withArgName("profile").hasArg().withDescription("spring profile (default: dev, cache)").create("p"));

        return options;
    }

    protected abstract void parseOtherParams(CommandLine commandLine) throws ParseException;
}
