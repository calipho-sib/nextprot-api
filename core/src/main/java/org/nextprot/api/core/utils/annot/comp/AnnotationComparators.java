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

    public static int compareNullableComparableObject(Comparable o1, Comparable o2) {

        return compareNullableComparableObject(o1, o2, true);
    }

    public static int compareNullableComparableObject(Comparable o1, Comparable o2, boolean asc) {

        int cmp;

        if (Objects.equals(o1, o2)) return 0;

        if (o1 == null)
            cmp = -1;
        else if (o2 == null)
            cmp = 1;
        else
            cmp = o1.compareTo(o2);

        return (asc) ? cmp : -cmp;
    }
}
