package org.nextprot.api.tasks.annotcat;

import org.apache.commons.cli.*;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.AnnotationCategoryStringWriter;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Export the hierarchy of AnnotationCategory elements as a tree
 *
 * Created by fnikitin on 13/11/15.
 */
public class AnnotationCategoryExporterApp {

    private static final Logger LOGGER = Logger.getLogger(AnnotationCategoryExporterApp.class.getSimpleName());
    private static final String DEFAULT_TREE_NAME = "annotation-category-tree";

    private final AnnotationCategoryStringWriter visitor;
    private final String outputFileName;

    private AnnotationCategoryExporterApp(String[] args) throws ParseException {

        Options options = createOptions();
        CommandLine commandLine = parseCommandLine(options, args);

        if (commandLine.hasOption("help")) {

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(AnnotationCategoryExporterApp.class.getSimpleName()+ " <file>", options);
            System.exit(0);
        }

        outputFileName = parseOutputFileName(commandLine);
        visitor = parseTreeVisitor(commandLine);
    }

    private Options createOptions() {

        Options options = new Options();

        Option help = new Option("help", "print this message");

        // it's a shame we cannot chain the calls together in the real builder pattern
        OptionBuilder.withArgName("dot|graphml|labels");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("format for output tree ('labels' by default)");
        Option format = OptionBuilder.create("format");

        OptionBuilder.withArgName("name");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("tree name ('"+DEFAULT_TREE_NAME+"' by default)");
        Option treeName = OptionBuilder.create("treename");

        options.addOption(help);
        options.addOption(format);
        options.addOption(treeName);

        return options;
    }

    private CommandLine parseCommandLine(Options options, String[] args) throws ParseException {

        CommandLineParser parser = new PosixParser();
        return parser.parse(options, args);
    }

    private String parseOutputFileName(CommandLine commandLine) throws ParseException {

        if (commandLine.getArgList().isEmpty()) {
            throw new ParseException("missing output file");
        }
        if (commandLine.getArgList().size()>1) {
            LOGGER.warning("Warning: only considering the first argument as output filename");
        }
        return (String)commandLine.getArgList().get(0);
    }

    private AnnotationCategoryStringWriter parseTreeVisitor(CommandLine line) throws ParseException {

        String treeName = DEFAULT_TREE_NAME;

        if (line.hasOption("treename")) {
            treeName = line.getOptionValue("treename");
        }

        AnnotationCategoryStringWriter tv;
        if (line.hasOption("format")) {

            String format = line.getOptionValue("format");

            if ("graphml".equalsIgnoreCase(format)) {
                tv = new AnnotationCategoryStringWriterGraphML(treeName);
            }
            else if ("dot".equalsIgnoreCase(format.toLowerCase())) {
                tv = new AnnotationCategoryStringWriterDot(treeName);
            }
            else if ("labels".equalsIgnoreCase(format.toLowerCase())) {
                tv = new AnnotationCategoryStringWriterLabels(treeName);
            }
            else {
                throw new IllegalArgumentException(format+": unknown output graph format");
            }
        } else {
            tv = new AnnotationCategoryStringWriterLabels(treeName);
        }

        return tv;
    }

    private String export() {

        visitor.visitNode(AnnotationCategory.ROOT);

        return visitor.writeString();
    }

    public static void main(String[] args) throws FileNotFoundException {

        try {
            AnnotationCategoryExporterApp app = new AnnotationCategoryExporterApp(args);

            String treeContent = app.export();

            try (PrintWriter pw = new PrintWriter(app.outputFileName)) {
                pw.print(treeContent);
            }
            LOGGER.info("Annotation categories have been successfully written in "+app.outputFileName);
        } catch (ParseException e) {

            LOGGER.severe(e.getMessage()+": parsing failed.");
        }
    }
}
