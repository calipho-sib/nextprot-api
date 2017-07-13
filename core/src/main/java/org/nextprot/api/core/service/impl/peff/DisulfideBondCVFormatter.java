package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.EnumSet;

/**
 * A disulfide bond type modification
 *
 * Created by fnikitin on 05/05/15.
 */
public class DisulfideBondCVFormatter extends PTMInfoFormatter {

    public DisulfideBondCVFormatter() {

        super(EnumSet.of(AnnotationCategory.DISULFIDE_BOND), SequenceDescriptorKey.MOD_RES);
    }

    @Override
    protected final String getModAccession(Annotation annotation) {

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