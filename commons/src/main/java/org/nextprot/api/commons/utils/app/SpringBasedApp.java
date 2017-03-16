package org.nextprot.api.commons.utils.app;


import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Base class for command line applications needing neXtProt spring services
 *
 * Concrete applications will need to implement <code>execute()</code>.
 * <p>
 * Any nextprot services are available from SpringConfig object via getBean() method.
 *
 * @param <P> the command line parser class type
 */
public abstract class SpringBasedApp<P extends CommandLineSpringParser> {

    private static final Logger LOGGER = Logger.getLogger(SpringBasedApp.class);

    private final P argumentParser;
    private final SpringConfig config;

    public SpringBasedApp(String[] args) throws ParseException {

        argumentParser = newCommandLineParser();
        config = argumentParser.parseSpringConfig(args);
    }

    /** Create new instance of command line parser */
    protected abstract P newCommandLineParser();

    protected final P getCommandLineParser() {

        return argumentParser;
    }

    protected final SpringConfig getConfig() {

        return config;
    }

    public void run() throws IOException {

        startApplicationContext();
        execute();
        stopApplicationContext();
    }

    /** The execution logic */
    protected abstract void execute() throws IOException;

    protected void startApplicationContext() {

        LOGGER.info("starting spring application context...");
        config.startApplicationContext();
        LOGGER.info("spring application context started");
    }

    protected void stopApplicationContext() {

        LOGGER.info("closing spring application context...");
        config.stopApplicationContext();
        LOGGER.info("spring application context closed");
    }
}
