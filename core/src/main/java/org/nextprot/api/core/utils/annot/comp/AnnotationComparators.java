package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;

public class AnnotationComparators {

    public static Comparator<Annotation> newComparator(AnnotationCategory annotationCategory, Isoform canonicalIsoform) {

        switch (annotationCategory) {
            case VARIANT:
            case MUTAGENESIS:
                return new ByIsoformPositionComparator(canonicalIsoform)
                        .thenComparing(a -> a.getVariant().getVariant());
            default:
                return new ByIsoformPositionComparator(canonicalIsoform)
                        .thenComparing(Annotation::getAnnotationId);
        }
    }
}
