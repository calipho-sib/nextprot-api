package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;
import java.util.Set;

/**
 * A disulfide bond type modification
 *
 * Created by fnikitin on 05/05/15.
 */
class DisulfideBondPeffFormatter extends IsoformPTMPeffFormatter {

    private static final Set<AnnotationApiModel> SUPPORTED_MODELS = EnumSet.of(AnnotationApiModel.DISULFIDE_BOND);

    DisulfideBondPeffFormatter() {

        super(SUPPORTED_MODELS, PeffKey.MOD_RES);
    }

    @Override
    protected final String getModName(Annotation annotation) {

        return "Disulfide";
    }

    @Override
    public String asPeffValue(Isoform isoform, Annotation... annotations) {

        StringBuilder sb = new StringBuilder();

        for (Annotation annotation : annotations) {

            if (support(annotation))
                sb.append("(").append(annotation.getStartPositionForIsoform(isoform.getUniqueName())).append("|")
                    .append("Disulfide").append(")")
                    .append("(").append(annotation.getEndPositionForIsoform(isoform.getUniqueName())).append("|")
                    .append("Disulfide").append(")");
        }

        return sb.toString();
    }
}