package org.nextprot.api.commons.app;


import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Base class for command line applications needing neXtProt spring services
 *
 * Concrete applications will need to implement <code>execute()</code>.
 * <p>
 * Any nextprot services are available from SpringConfig object via getBean() method.
 *
 * @param <P> the command line parser class type
 */
public abstract class SpringBasedTask<P extends CommandLineSpringParser> {

    private static final Logger LOGGER = Logger.getLogger(SpringBasedTask.class.getName());

    private final P argumentParser;
    private final SpringConfig config;
    private final Map<String, Object> parameters;

    public SpringBasedTask(String[] args) throws ParseException {

        argumentParser = newCommandLineParser();
        config = argumentParser.parseSpringConfig(args);

        parameters = new HashMap<>();
    }

    /** Create new instance of command line parser */
    protected abstract P newCommandLineParser();

    protected void putParams(Map<String, Object> parameters) { }

    protected final P getCommandLineParser() {

        return argumentParser;
    }

    public <T> T getBean(Class<T> requiredType) {

        return config.getBean(requiredType);
    }

    public void run() throws IOException {

        startApplicationContext();

        parameters.put("spring profiles", config.getProfiles());
        putParams(parameters);
        LOGGER.info("Parameters: " + parameters);

        LOGGER.info("task started...");
        execute();
        LOGGER.info("task completed");

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
