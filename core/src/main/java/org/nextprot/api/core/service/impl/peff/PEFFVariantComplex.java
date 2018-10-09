package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * A complex variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public class PEFFVariantComplex extends AnnotationBasedPEFFInformation {

    public PEFFVariantComplex(String isoformAccession, List<Annotation> isoformAnnotations) {

        super(isoformAccession, isoformAnnotations, EnumSet.of(AnnotationCategory.VARIANT), Key.VARIANT_COMPLEX);
    }

    @Override
    protected Comparator<Annotation> createAnnotationComparator(String isoformAccession) {

        return super.createAnnotationComparator(isoformAccession)
                .thenComparingInt(a -> a.getEndPositionForIsoform(isoformAccession))
                .thenComparing(a -> a.getVariant().getVariant());
    }

    @Override
    protected boolean selectAnnotation(Annotation annotation) {

        return super.selectAnnotation(annotation) &&
                isPositional(annotation) &&
                (annotation.getStartPositionForIsoform(isoformAccession) < annotation.getEndPositionForIsoform(isoformAccession) ||
                    annotation.getVariant().getVariant().length() == 0 || // deletion
                    annotation.getVariant().getVariant().length() > 1     // insertion
                );
    }

    @Override
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

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