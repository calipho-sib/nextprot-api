package org.nextprot.api.tasks.commons;

import org.nextprot.api.commons.constants.AbstractTreeVisitor;
import org.nextprot.api.commons.constants.AnnotationCategory;

/**
 * Export tree as graph dot format (http://www.graphviz.org/content/dot-language).
 *
 * Created by pam on 26/09/16.
 */
class TreeVisitorLabels extends AbstractTreeVisitor {

    TreeVisitorLabels(String graphName) {

        super(graphName);
    }

    @Override
    protected void writeEdge(AnnotationCategory parent, AnnotationCategory child) {

    	int level = child.getAllParentsButRoot().size();
    	for (int i =0;i<level;i++) sb.append("\t");
        sb.append(child.getLabel());
        sb.append("\n");
    }

    @Override
    public String asString() {

        StringBuilder content = new StringBuilder();

        content.append("graph ").append(getName()).append(" {\n");
        content.append("\tnodesep=0.1; ranksep=0.5; ratio=compress; size=\"7.5,10\"; center=true; node [style=\"rounded,filled\", width=0, height=0, shape=box, fillcolor=\"#E5E5E5\", concentrate=true]\n");
        content.append(sb.toString()).append("}");

        return content.toString();
    }
}
