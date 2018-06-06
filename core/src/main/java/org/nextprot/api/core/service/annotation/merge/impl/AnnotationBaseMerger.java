package org.nextprot.api.core.service.annotation.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.AnnotationMerger;
import org.nextprot.commons.constants.QualityQualifier;

public abstract class AnnotationBaseMerger implements AnnotationMerger {

    @Override
    public Annotation merge(Annotation annotation1, Annotation annotation2) {

        Annotation dest = getDestAnnotation(annotation1, annotation2);
        Annotation source = getSourceAnnotation(annotation1, annotation2);

        updateDestEvidences(dest, source);
        updateDestAnnotationHash(dest, source);
        updateDestIsoformSpecificityName(dest, source);
        updateDestQualityQualifier(dest, source);
        updateDestBioObject(dest, source);

        return dest;
    }

    /** Get annotation where all sources should be merged to */
    protected abstract Annotation getDestAnnotation(Annotation annotation1, Annotation annotation2);

    /** Get source annotation */
    protected abstract Annotation getSourceAnnotation(Annotation annotation1, Annotation annotation2);

    /** Update dest evidences with sources evidences */
    protected abstract void updateDestEvidences(Annotation dest, Annotation source);

    /** Update dest annotation hash */
    protected abstract void updateDestAnnotationHash(Annotation dest, Annotation source);

    /** Update dest isoform specificity name (variant name) */
    protected abstract void updateDestIsoformSpecificityName(Annotation dest, Annotation source);

    /** Reset dest qualityqualifier to gold if there is at least one gold source */
    private void updateDestQualityQualifier(Annotation dest, Annotation source) {

        if (dest.getQualityQualifier() == null || QualityQualifier.valueOf(dest.getQualityQualifier()) != QualityQualifier.GOLD) {

            if (source.getQualityQualifier() != null && source.getQualityQualifier().equals(QualityQualifier.GOLD.name())) {
                dest.setQualityQualifier(QualityQualifier.GOLD.name());
            }
        }
    }

    protected abstract void updateDestBioObject(Annotation dest, Annotation source);
}
