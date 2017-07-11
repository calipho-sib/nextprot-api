package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A complex variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
class VariantComplexFormatter extends AnnotationBasedSequenceInfoFormatter {

    public VariantComplexFormatter() {

        super(EnumSet.of(AnnotationCategory.VARIANT), SequenceDescriptorKey.VARIANT_COMPLEX);
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getEndPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getVariant().getVariant());
    }
}