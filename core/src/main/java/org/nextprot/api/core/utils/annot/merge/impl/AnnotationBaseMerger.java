package org.nextprot.api.core.utils.annot.merge.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.AnnotationMerger;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.List;

public abstract class AnnotationBaseMerger implements AnnotationMerger {

    @Override
    public Annotation merge(Annotation annotation1, Annotation annotation2, Annotation... others) {

        Annotation dest = getDestAnnotation(annotation1, annotation2, others);
        List<Annotation> sources = getSourceAnnotations(annotation1, annotation2, others);

        updateDestEvidences(dest, sources);
        updateDestAnnotationHash(dest, sources);
        updateDestAnnotationName(dest, sources);
        updateDestIsoformSpecificityName(dest, sources);
        updateDestQualityQualifier(dest, sources);
        updateDestBioObject(dest, sources);

        return dest;
    }

    /** Get annotation where all sources should be merged to */
    protected abstract Annotation getDestAnnotation(Annotation annotation1, Annotation annotation2, Annotation... others);

    /** Get source annotations */
    protected abstract List<Annotation> getSourceAnnotations(Annotation annotation1, Annotation annotation2, Annotation... others);

    /** Update dest evidences with sources evidences */
    protected abstract void updateDestEvidences(Annotation dest, List<Annotation> sources);

    /** Update dest annotation hash */
    protected abstract void updateDestAnnotationHash(Annotation dest, List<Annotation> sources);

    /** Update dest annotation name */
    protected abstract void updateDestAnnotationName(Annotation dest, List<Annotation> sources);

    /** Update dest isoform specificity name (variant name) */
    protected abstract void updateDestIsoformSpecificityName(Annotation dest, List<Annotation> sources);

    /** Reset dest qualityqualifier to gold if there is at least one gold source */
    private void updateDestQualityQualifier(Annotation dest, List<Annotation> sources) {

        if (dest.getQualityQualifier() == null || QualityQualifier.valueOf(dest.getQualityQualifier()) != QualityQualifier.GOLD) {

            for (Annotation source : sources) {

                if (source.getQualityQualifier() != null && source.getQualityQualifier().equals(QualityQualifier.GOLD.name())) {
                    dest.setQualityQualifier(QualityQualifier.GOLD.name());
                    break;
                }
            }
        }
    }

    protected abstract void updateDestBioObject(Annotation dest, List<Annotation> sources);
}
