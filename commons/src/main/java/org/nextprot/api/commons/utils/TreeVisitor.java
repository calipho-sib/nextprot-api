package org.nextprot.api.commons.utils;

/**
 * Created by fnikitin on 17/06/15.
 */
public interface TreeVisitor<T> {

    void visit(T node);
    String asString();
    String getName();
}
