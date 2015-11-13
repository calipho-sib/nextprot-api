package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.utils.TreeVisitor;

/**
 * A base class to export AnnotationCategory element in a tree
 *
 * Created by fnikitin on 17/06/15.
 */
public abstract class AbstractTreeVisitor implements TreeVisitor<AnnotationCategory> {

    protected final StringBuilder sb;
    private final String name;

    protected AbstractTreeVisitor(String graphName) {

        name = graphName;
        sb = new StringBuilder();
    }

    @Override
    public void visit(AnnotationCategory node) {

        writeNode(node);

        for (AnnotationCategory child : node.getChildren()) {

            writeEdge(node, child);

            visit(child);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    protected void writeNode(AnnotationCategory parent) {
    }

    protected abstract void writeEdge(AnnotationCategory parent, AnnotationCategory child);
}
