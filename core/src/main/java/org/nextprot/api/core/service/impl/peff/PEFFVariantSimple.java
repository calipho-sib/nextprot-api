package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;
import java.util.EnumSet;

/**
 * A variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public class PEFFVariantSimple extends AnnotationBasedPEFFInformation {

    public PEFFVariantSimple(Entry entry, String isoformAccession) {

        super(entry, isoformAccession, EnumSet.of(AnnotationCategory.VARIANT), Key.VARIANT_SIMPLE);
    }

    @Override
    protected Comparator<Annotation> createAnnotationComparator(String isoformAccession) {

        return super.createAnnotationComparator(isoformAccession)
                .thenComparing(Comparator.comparing(a -> a.getVariant().getVariant()));
    }

    @Override
    protected boolean doHandleAnnotation(Annotation annotation) {

        return super.doHandleAnnotation(annotation) &&
                annotation.getStartPositionForIsoform(isoformAccession).intValue() == annotation.getEndPositionForIsoform(isoformAccession).intValue() &&
                annotation.getVariant().getVariant().length() == 1;
    }

    @Override
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

        sb
                .append("(")
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getVariant().getVariant())
                .append(")")
        ;
    }
}