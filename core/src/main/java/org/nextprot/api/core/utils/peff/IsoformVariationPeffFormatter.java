package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
class IsoformVariationPeffFormatter extends IsoformAnnotationPeffFormatter {

    public IsoformVariationPeffFormatter() {

        super(EnumSet.of(AnnotationApiModel.VARIANT), PeffKey.VARIANT);
    }

    @Override
    public String asPeffValue(Isoform isoform, Annotation... annotations) {

        StringBuilder sb = new StringBuilder();

        for (Annotation annotation : annotations) {

            if (support(annotation))
                sb.append("(").append(annotation.getStartPositionForIsoform(isoform.getUniqueName()))
                    .append("|").append(annotation.getEndPositionForIsoform(isoform.getUniqueName())).append("|")
                    .append(annotation.getVariant().getVariant()).append(")");
        }

        return sb.toString();
    }
}