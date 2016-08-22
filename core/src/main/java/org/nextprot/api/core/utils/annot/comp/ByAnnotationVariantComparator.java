package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;

/**
 * Compare any annotations containing getVariant() informations
 */
class ByAnnotationVariantComparator implements Comparator<Annotation> {

    private final Comparator<Annotation> comparator;

    ByAnnotationVariantComparator() {

        comparator = new ByFeaturePositionComparator()
                .thenComparing(a -> a.getVariant().getOriginal())
                .thenComparing(a -> a.getVariant().getVariant());
    }

    @Override
    public int compare(Annotation a1, Annotation a2) {

        if (a1.getVariant() != null && a2.getVariant() != null)
            return comparator.compare(a1, a2);
        return 0;
    }
}
