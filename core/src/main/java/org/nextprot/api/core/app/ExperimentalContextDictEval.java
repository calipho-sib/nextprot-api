package org.nextprot.api.core.app;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.github.jamm.MemoryMeter;
import org.nextprot.api.commons.utils.app.CommandLineSpringParser;
import org.nextprot.api.commons.utils.app.SpringBasedApp;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;

import java.io.IOException;
import java.util.Map;

// -javaagent:/Users/fnikitin/Projects/jamm/target/jamm-0.3.2-SNAPSHOT.jar
public class ExperimentalContextDictEval extends SpringBasedApp<ExperimentalContextDictEval.CommandLineParser> {

    private ExperimentalContextDictEval(String[] args) throws ParseException {

        super(args);
    }

    @Override
    protected ExperimentalContextDictEval.CommandLineParser newCommandLineParser() {

        return new CommandLineParser();
    }

    @Override
    protected void execute() throws IOException {

        ExperimentalContextDictionaryService bean = getBean(ExperimentalContextDictionaryService.class);
        Map<Long, ExperimentalContext> dict = bean.getAllExperimentalContexts();

        // 1. git clone https://github.com/jbellis/jamm.git <path to>/ ; cd <path to>/jamm ; ant jar ; add dependency to this jar
        // 2. start the JVM with "-javaagent:<path to>/jamm.jar"
        MemoryMeter memMeter = new MemoryMeter();

        if (getCommandLineParser().isDebugMode()) {
            memMeter.enableDebug();
        }

        long shallowMemory = memMeter.measure(dict);
        long deepMemory = memMeter.measureDeep(dict);
        long childrenCount = memMeter.countChildren(dict);

        StringBuilder sb = new StringBuilder("experimental-context-dictionary memory allocation: ");

        sb
                .append("shallow=").append(shallowMemory).append("B")
                .append(", deep=").append((int)Math.ceil(deepMemory / 1024.)).append("KB")
                .append(", children#=").append(childrenCount);

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
            new ExperimentalContextDictEval(args).run();
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
            super("experimental-context-dictionary-eval");
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
