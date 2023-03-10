package org.nextprot.api.core.app;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.github.jamm.MemoryMeter;
import org.nextprot.api.commons.app.CommandLineSpringParser;
import org.nextprot.api.commons.app.SpringBasedTask;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;

import java.io.IOException;
import java.util.Map;

/**
 * <h3>About estimating Java Object Sizes with Instrumentation</h3>
 * Setting jamm as -javaagent is now optional
 * If instrumentation is available, use it, otherwise guess the size using sun.misc.Unsafe; if that is unavailable,
 * guess using predefined specifications
 * <pre>-javaagent: $path/jamm/target/jamm-0.3.2-SNAPSHOT.jar</pre>
 */
public class ExperimentalContextDictAnalyserTask extends SpringBasedTask<ExperimentalContextDictAnalyserTask.CommandLineParser> {

    private ExperimentalContextDictAnalyserTask(String[] args) throws ParseException {

        super(args);
    }

    @Override
    protected ExperimentalContextDictAnalyserTask.CommandLineParser newCommandLineParser() {

        return new CommandLineParser();
    }

    @Override
    protected void execute() throws IOException {

        ExperimentalContextDictionaryService bean = getBean(ExperimentalContextDictionaryService.class);
        Map<Long, ExperimentalContext> dict = bean.getIdExperimentalContextMap();

        /* If instrumentation is available, use it, otherwise guess the size using sun.misc.Unsafe; if that is unavailable,
         * guess using predefined specifications -> setting jamm as -javaagent is now optional */
        MemoryMeter memMeter = new MemoryMeter().withGuessing(MemoryMeter.Guess.FALLBACK_BEST);

        if (getCommandLineParser().isDebugMode()) {
            memMeter = memMeter.enableDebug();
        }

        long shallowMemory = memMeter.measure(dict);
        long deepMemory = memMeter.measureDeep(dict);
        long childrenCount = memMeter.countChildren(dict);

        StringBuilder sb = new StringBuilder("experimental-context-dictionary memory allocation: ");

        sb
                .append("shallow=").append(shallowMemory).append("B")
                .append(", deep=").append((int)Math.ceil(deepMemory / 1024.)).append("KB")
                .append(", children#=").append(childrenCount).append("\n");

        System.out.printf(sb.toString());
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
            new ExperimentalContextDictAnalyserTask(args).run();
        } catch(Exception e) {

            e.printStackTrace();

            System.exit(1);
        }
    }

    /**
     * Parse arguments and provides MainConfig object
     *
     * Created by fnikitin on 09/08/16.
     */
    static class CommandLineParser extends CommandLineSpringParser {

        private boolean debugMode;

        public CommandLineParser() {
            super(ExperimentalContextDictAnalyserTask.class.getSimpleName());
        }

        @Override
        protected Options createOptions() {

            Options options = super.createOptions();

            //noinspection AccessStaticViaInstance
            options.addOption(OptionBuilder.withArgName("debug").withDescription("debug mode").create("d"));

            return options;
        }

        @Override
        protected void parseOtherParams(CommandLine commandLine) {

            debugMode = commandLine.hasOption("d");
        }

        public boolean isDebugMode() {
            return debugMode;
        }
    }
}
