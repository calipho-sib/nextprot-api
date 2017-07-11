package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A disulfide bond type modification
 *
 * Created by fnikitin on 05/05/15.
 */
class DisulfideBondCVFormatter extends PTMInfoFormatter {

    DisulfideBondCVFormatter() {

        super(EnumSet.of(AnnotationCategory.DISULFIDE_BOND), SequenceDescriptorKey.MOD_RES);
    }

    @Override
    protected final String getModName(Annotation annotation) {

        return "Disulfide";
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation disulfideBondAnnotation, StringBuilder sb) {

        sb
                .append(disulfideBondAnnotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append("Disulfide")
                .append(")")
                .append("(")
                .append(disulfideBondAnnotation.getEndPositionForIsoform(isoformAccession))
                .append("|")
                .append("Disulfide");
    }
}