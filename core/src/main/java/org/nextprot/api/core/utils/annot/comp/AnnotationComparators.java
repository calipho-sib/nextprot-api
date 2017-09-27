package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class AnnotationComparators {

    public static Comparator<Annotation> newComparator(AnnotationCategory annotationCategory) {

        switch (annotationCategory) {
            case MUTAGENESIS:
            case VARIANT:
                return new ByAnnotationVariantComparator();
            default:
                return new ByFeaturePositionComparator()
                        .thenComparing(Annotation::getAnnotationId);
        }
    }

    public static Comparator<Annotation> newPhenotypicVariationComparator(Map<String, Annotation> annotationByHash) {

        return new ByAnnotationSubjectComparator(annotationByHash)
                .thenComparing(new ByAnnotationBioObjectComparator(annotationByHash))
                .thenComparing(Annotation::getCvTermName);
    }
}
