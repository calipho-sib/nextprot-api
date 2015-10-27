package org.nextprot.api.commons.constants;

/**
 * Export tree as graph dot format (http://www.graphviz.org/content/dot-language).
 *
 * Created by fnikitin on 17/06/15.
 */
public class TreeVisitorDot extends AbstractTreeVisitor {
    //dot -Teps annotations.dot -o annotations.ps; pstopdf annotations.ps annotations.pdf
    public TreeVisitorDot(String graphName) {

        super(graphName);
    }

    @Override
    protected void writeEdge(AnnotationCategory parent, AnnotationCategory child) {

        sb.append("\t");
        sb.append(parent.getApiTypeName());
        sb.append(" -- ");
        sb.append(child.getApiTypeName()).append(" ;\n");
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
