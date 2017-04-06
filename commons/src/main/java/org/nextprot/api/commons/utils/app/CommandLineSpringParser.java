package org.nextprot.api.commons.utils.app;

import org.apache.commons.cli.*;

/**
 * Base command line parser for Spring application
 */
public class CommandLineSpringParser {

    private final String appName;
    private final HelpFormatter formatter;
    private final Options options;

    public CommandLineSpringParser(String appName) {

        this.appName = appName;

        options = createOptions();
        formatter = new HelpFormatter();
    }

    /**
     * Parse spring specific args and put left-over non-recognized options and arguments into collector
     * @param args arguments to parse
     * @return SpringConfig
     * @throws ParseException
     */
    public SpringConfig parseSpringConfig(String[] args) throws ParseException {

        CommandLineParser parser = new GnuParser();

        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("h")) {
            formatter.printHelp( appName, options );
            System.exit(0);
        }

        SpringConfig springConfig = (commandLine.hasOption("p")) ?
                new SpringConfig(commandLine.getOptionValue("p")) : new SpringConfig();

        parseOtherParams(commandLine);

        return springConfig;
    }

    protected Options createOptions() {

        Options options = new Options();

        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder.withArgName("help").withDescription("display help").create("h"));
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder.withArgName("profile").hasArg().withDescription("spring profile (default: dev, cache)").create("p"));

        return options;
    }

    protected void parseOtherParams(CommandLine commandLine) throws ParseException { }
}
