package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;
import java.util.Set;

/**
 * A disulfide bond type modification
 *
 * Created by fnikitin on 05/05/15.
 */
class DisulfideBond extends IsoformPTM {

    private static final Set<AnnotationApiModel> SUPPORTED_MODELS = EnumSet.of(AnnotationApiModel.DISULFIDE_BOND);

    DisulfideBond(String isoformId, Annotation annotation) {

        super(isoformId, annotation, SUPPORTED_MODELS, "Disulfide");
    }

    @Override
    public boolean isPSI() {
        return false;
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(getModificationName()).append(")")
                .append("(").append(getEnd()).append("|").append(getModificationName()).append(")");
        return sb.toString();
    }

    public static boolean isModelSupported(AnnotationApiModel model) {

        return SUPPORTED_MODELS.contains(model);
    }
}