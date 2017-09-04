package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.Comparator;
import java.util.EnumSet;

/**
 * A complex variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public class VariantComplexFormatter extends AnnotationBasedSequenceInfoFormatter {

    public VariantComplexFormatter() {

        super(EnumSet.of(AnnotationCategory.VARIANT), SequenceDescriptorKey.VARIANT_COMPLEX);
    }

    @Override
    protected Comparator<Annotation> createAnnotationComparator(String isoformAccession) {

        return super.createAnnotationComparator(isoformAccession)
                .thenComparing(Comparator.comparingInt(a -> a.getEndPositionForIsoform(isoformAccession)))
                .thenComparing(Comparator.comparing(a -> a.getVariant().getVariant()));
    }

    @Override
    protected boolean doHandleAnnotation(Annotation annotation, String isoformAccession) {

        return super.doHandleAnnotation(annotation, isoformAccession) &&
                (
                    annotation.getStartPositionForIsoform(isoformAccession) < annotation.getEndPositionForIsoform(isoformAccession) ||
                    annotation.getVariant().getVariant().length() == 0 || // deletion
                    annotation.getVariant().getVariant().length() > 1     // insertion
                );
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append("(")
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getEndPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getVariant().getVariant())
                .append(")")
        ;
    }
}