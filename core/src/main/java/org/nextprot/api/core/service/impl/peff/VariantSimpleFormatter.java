package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.Comparator;
import java.util.EnumSet;

/**
 * A variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public class VariantSimpleFormatter extends AnnotationBasedSequenceInfoFormatter {

    public VariantSimpleFormatter() {

        super(EnumSet.of(AnnotationCategory.VARIANT), SequenceDescriptorKey.VARIANT_SIMPLE);
    }

    @Override
    protected Comparator<Annotation> createAnnotationComparator(String isoformAccession) {

        return super.createAnnotationComparator(isoformAccession)
                .thenComparing(Comparator.comparing(a -> a.getVariant().getVariant()));
    }

    @Override
    protected boolean doHandleAnnotation(Annotation annotation, String isoformAccession) {

        return super.doHandleAnnotation(annotation, isoformAccession) &&
                annotation.getStartPositionForIsoform(isoformAccession).intValue() == annotation.getEndPositionForIsoform(isoformAccession).intValue() &&
                annotation.getVariant().getVariant().length() == 1;
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append("(")
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getVariant().getVariant())
                .append(")")
        ;
    }
}