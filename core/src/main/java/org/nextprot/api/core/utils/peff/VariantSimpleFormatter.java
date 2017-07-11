package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
class VariantSimpleFormatter extends AnnotationBasedSequenceInfoFormatter {

    public VariantSimpleFormatter() {

        super(EnumSet.of(AnnotationCategory.VARIANT), SequenceDescriptorKey.VARIANT_SIMPLE);
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getVariant().getVariant());
    }
}