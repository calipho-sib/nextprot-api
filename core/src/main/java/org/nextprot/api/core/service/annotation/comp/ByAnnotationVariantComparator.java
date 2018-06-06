package org.nextprot.api.core.service.annotation.comp;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.AnnotationUtils;

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

        if (a1.getVariant() == null)
            throw new NextProtException("undefined AnnotationVariant for annotation:\n"+ AnnotationUtils.toString(a1));

        if (a2.getVariant() == null)
            throw new NextProtException("undefined AnnotationVariant for annotation:\n"+ AnnotationUtils.toString(a2));

        return comparator.compare(a1, a2);
    }
}
