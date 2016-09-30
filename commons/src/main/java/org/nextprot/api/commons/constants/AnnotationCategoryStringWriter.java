package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.utils.TreeVisitor;

/**
 * A base class to export AnnotationCategory element in a tree
 *
 * Created by fnikitin on 17/06/15.
 */
public abstract class AnnotationCategoryStringWriter implements TreeVisitor<AnnotationCategory> {

    private final StringBuilder sb;
    private final String name;

    protected AnnotationCategoryStringWriter(String graphName) {

        name = graphName;
        sb = new StringBuilder();
    }

    @Override
    public void visitNode(AnnotationCategory node) {

        writeNode(node, sb);

        for (AnnotationCategory child : node.getChildren()) {

            writeEdge(node, child, sb);

            visitNode(child);
        }
    }

    protected String getName() {
        return name;
    }

    protected void writeNode(AnnotationCategory parent, StringBuilder sb) { }

    protected abstract void writeEdge(AnnotationCategory parent, AnnotationCategory child, StringBuilder sb);

    protected String getHeader() { return ""; }

    protected String getFooter() { return ""; }

    /**
     * @return the content of the visited tree
     */
    public String writeString() {

        StringBuilder content = new StringBuilder();

        content
                .append(getHeader())
                .append(sb.toString())
                .append(getFooter());

        return content.toString();
    }
}
