package org.nextprot.api.commons.constants;

import org.nextprot.api.commons.utils.TreeVisitor;

/**
 * Created by fnikitin on 17/06/15.
 */
abstract class AbstractTreeVisitor implements TreeVisitor<AnnotationApiModel> {

    protected final StringBuilder sb;
    private final String name;

    protected AbstractTreeVisitor(String graphName) {

        name = graphName;
        sb = new StringBuilder();
    }

    @Override
    public void visit(AnnotationApiModel node) {

        writeNode(node);

        for (AnnotationApiModel child : node.getChildren()) {

            writeEdge(node, child);

            visit(child);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    protected void writeNode(AnnotationApiModel parent) {
    }

    protected abstract void writeEdge(AnnotationApiModel parent, AnnotationApiModel child);
}
