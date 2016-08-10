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

        updateDestQualityQualifier(dest);

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

    /** Reset dest qualityqualifier to gold if there is at least one gold evidence */
    private void updateDestQualityQualifier(Annotation dest) {

        if (dest.getQualityQualifier() == null || QualityQualifier.valueOf(dest.getQualityQualifier()) != QualityQualifier.GOLD)
            if (dest.getEvidences().stream().anyMatch(e -> QualityQualifier.valueOf(e.getQualityQualifier()) == QualityQualifier.GOLD))
                dest.setQualityQualifier(QualityQualifier.GOLD.toString());
    }
}
