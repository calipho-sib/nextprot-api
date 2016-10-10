package org.nextprot.api.commons.utils;

/**
 * Called each time a node is visited
 *
 * Created by fnikitin on 17/06/15.
 */
public interface TreeVisitor<T> {

    void visitNode(T node);
}
