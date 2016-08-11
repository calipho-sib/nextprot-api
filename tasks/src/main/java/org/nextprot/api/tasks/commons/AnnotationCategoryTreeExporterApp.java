package org.nextprot.api.tasks.commons;

import org.apache.commons.cli.*;
import org.nextprot.api.commons.constants.AbstractTreeVisitor;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.TreeVisitor;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Export the hierarchy of AnnotationCategory elements as a tree
 *
 * Created by fnikitin on 13/11/15.
 */
public class AnnotationCategoryTreeExporterApp {

    private static final Options OPTIONS = createOptions();
    private static final String DEFAULT_TREE_NAME = "annotation-category-tree";

    private final TreeVisitor<AnnotationCategory> visitor;

    public AnnotationCategoryTreeExporterApp(TreeVisitor<AnnotationCategory> visitor) {

        this.visitor = visitor;
    }

    public String export() {

        visitor.visit(AnnotationCategory.ROOT);

        return visitor.asString();
    }

    public static void main(String[] args) throws FileNotFoundException {

        List<String> argList = new ArrayList<>();

        AnnotationCategoryTreeExporterApp app = newAnnotationCategoryTreeExporterApp(args, argList);

        String treeContent = app.export();

        if (!argList.isEmpty()) {

            PrintWriter pw = new PrintWriter(argList.get(0));

            pw.print(treeContent);

            pw.close();
        } else {

            System.err.println("missing output file");
            System.exit(1);
        }
    }

    private static Options createOptions() {

        Options options = new Options();

        Option help = new Option("help", "print this message");
        Option format = OptionBuilder.withArgName("dot|graphml")
                .hasArg()
                .withDescription("format for output tree ('dot' by default)")
                .create("format");

        Option treeName = OptionBuilder.withArgName("name")
                .hasArg()
                .withDescription("tree name ('"+DEFAULT_TREE_NAME+"' by default)")
                .create("treename");

        options.addOption(help);
        options.addOption(format);
        options.addOption(treeName);

        return options;
    }

    private static AnnotationCategoryTreeExporterApp newAnnotationCategoryTreeExporterApp(String[] args, List<String> arguments) {

        CommandLineParser parser = new PosixParser();

        String treeName = DEFAULT_TREE_NAME;
        AbstractTreeVisitor visitor;

        try {
            CommandLine line = parser.parse(OPTIONS, args);

            arguments.addAll(line.getArgList());

            if (line.hasOption("help")) {

                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(AnnotationCategoryTreeExporterApp.class.getSimpleName()+ " <file>", OPTIONS );
                System.exit(0);
            }

            if (line.hasOption("treename")) {

                treeName = line.getOptionValue("treename");
            }

            if (line.hasOption("format")) {

                String format = line.getOptionValue("format");

                if (format.toLowerCase().equals("graphml")) {
                    visitor = new TreeVisitorGraphML(treeName);
                }
                else if (format.toLowerCase().equals("dot")) {
                    visitor = new TreeVisitorDot(treeName);
                }
                else {
                    throw new IllegalArgumentException(format+": unknown output graph format");
                }
            } else {
                visitor = new TreeVisitorDot(treeName);
            }
        }
        catch (ParseException exp) {

            throw new IllegalStateException(exp.getMessage()+": parsing failed.");
        }

        return new AnnotationCategoryTreeExporterApp(visitor);
    }
}
