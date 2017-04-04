package org.nextprot.api.core.app;

import org.apache.commons.cli.ParseException;
import org.github.jamm.MemoryMeter;
import org.nextprot.api.commons.utils.app.CommandLineSpringParser;
import org.nextprot.api.commons.utils.app.SpringBasedApp;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.service.ExperimentalContextDictionaryService;

import java.io.IOException;
import java.util.Map;

// -ea -javaagent:/Users/fnikitin/Projects/jamm/target/jamm-0.3.2-SNAPSHOT.jar
public class ExperimentalContextDictEval extends SpringBasedApp<CommandLineSpringParser> {

    private ExperimentalContextDictEval(String[] args) throws ParseException {

        super(args);
    }

    @Override
    protected CommandLineSpringParser newCommandLineParser() {

        return new CommandLineSpringParser("experimentalcontextdicteval");
    }

    @Override
    protected void execute() throws IOException {

        ExperimentalContextDictionaryService bean = getBean(ExperimentalContextDictionaryService.class);
        Map<Long, ExperimentalContext> dict = bean.getAllExperimentalContexts();

        // 1. git clone https://github.com/jbellis/jamm.git <path to>/ ; cd <path to>/jamm ; ant jar ; add dependency to this jar
        // 2. start the JVM with "-javaagent:<path to>/jamm.jar"
        MemoryMeter memMeter = new MemoryMeter();

        long memory = memMeter.measureDeep(dict);

        // 48_857_800 bytes (49MB)
        System.out.println("getAllExperimentalContexts memory="+memory);
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
}
