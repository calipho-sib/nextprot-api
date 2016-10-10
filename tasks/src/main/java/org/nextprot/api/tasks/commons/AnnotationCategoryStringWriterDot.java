package org.nextprot.api.tasks.commons;

import org.nextprot.api.commons.constants.AnnotationCategoryStringWriter;
import org.nextprot.api.commons.constants.AnnotationCategory;

/**
 * Export tree as graph dot format (http://www.graphviz.org/content/dot-language).
 *
 * Created by fnikitin on 17/06/15.
 */
class AnnotationCategoryStringWriterDot extends AnnotationCategoryStringWriter {

    //dot -Teps annotations.dot -o annotations.ps; pstopdf annotations.ps annotations.pdf
    AnnotationCategoryStringWriterDot(String graphName) {

        super(graphName);
    }

    @Override
    protected void writeEdge(AnnotationCategory parent, AnnotationCategory child, StringBuilder sb) {

        sb.append("\t");
        sb.append(parent.getApiTypeName());
        sb.append(" -- ");
        sb.append(child.getApiTypeName()).append(" ;\n");
    }

    @Override
    protected String getHeader() {

        StringBuilder header = new StringBuilder();

        header.append("graph ").append(getName()).append(" {\n");
        header.append("\tnodesep=0.1; ranksep=0.5; ratio=compress; size=\"7.5,10\"; center=true; node [style=\"rounded,filled\", width=0, height=0, shape=box, fillcolor=\"#E5E5E5\", concentrate=true]\n");

        return header.toString();
    }

    @Override
    protected String getFooter() {
        return "}";
    }
}
