package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * A variation located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public class Variation extends Position {

    private final String variant;

    public Variation(String isoformId, Annotation annotation) {

        super(isoformId, annotation);

        variant = annotation.getVariant().getVariant();
    }

    public String getVariant() {
        return variant;
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(getEnd()).append("|").append(variant).append(")");
        return sb.toString();
    }
}