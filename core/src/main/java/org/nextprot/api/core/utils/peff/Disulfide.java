package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * A disulfide bond type modification
 *
 * Created by fnikitin on 05/05/15.
 */
public class Disulfide extends Modification {

    public Disulfide(String isoformId, Annotation annotation) {

        super(isoformId, annotation);
    }

    public final String getModificationName() {

        return "Disulfide";
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(getModificationName()).append(")")
                .append("(").append(getEnd()).append("|").append(getModificationName()).append(")");
        return sb.toString();
    }
}