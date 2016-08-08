package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.utils.annot.AnnotationMerger;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Merge annotations by updating and returning target annotation with source annotations
 */
public class AnnotationUpdater implements AnnotationMerger {

    @Override
    public Annotation merge(Annotation target, Annotation source, Annotation... otherSources) {

        updateEvidences(target, source, otherSources);
        updateAnnotationHash(target, source, otherSources);
        updateAnnotationName(target, source);

        updateQualityQualifier(target);

        return target;
    }

    private void updateEvidences(Annotation target, Annotation source, Annotation... otherSources) {

        List<AnnotationEvidence> all = new ArrayList<>(target.getEvidences());

        all.addAll(source.getEvidences().stream().filter(e -> !target.getEvidences().contains(e)).collect(Collectors.toList()));

        for (Annotation src : otherSources) {

            // add only different evidences
            all.addAll(src.getEvidences().stream().filter(e -> !target.getEvidences().contains(e)).collect(Collectors.toList()));
        }

        target.setEvidences(all);
    }

    private void updateQualityQualifier(Annotation target) {

        // reset to gold if there is at least one gold evidence
        if (target.getEvidences().stream().anyMatch(e -> QualityQualifier.valueOf(e.getQualityQualifier()) == QualityQualifier.GOLD))
            target.setQualityQualifier(QualityQualifier.GOLD.name());
    }

    private void updateAnnotationHash(Annotation target, Annotation source, Annotation... otherSources) {

        String annotationHash = source.getAnnotationHash();

        if (annotationHash == null || annotationHash.isEmpty())
            throw new NextProtException("annotation hash was not computed for source "+source.getAnnotationName());

        if (otherSources.length>0) {

            checkUniqueAnnotationHash(annotationHash, otherSources);
        }

        target.setAnnotationHash(annotationHash);
    }

    private void updateAnnotationName(Annotation target, Annotation source) {

        target.setAnnotationName(source.getAnnotationName());
    }

    /**
     * Get annotation hash of given annotations
     * @param sources the source annotations
     * @return a unique annotation hash
     * @throws NextProtException is similar annotation sources do not have the same annotation hash
     */
    private void checkUniqueAnnotationHash(String annotationHash, Annotation... sources) {

        for (Annotation source : sources) {

            String hash = source.getAnnotationHash();

            if (hash == null || hash.isEmpty())
                throw new NextProtException("annotation hash was not computed for source "+source.getAnnotationName());
            else if (!annotationHash.equals(hash)) {
                throw new NextProtException("annotation hash differ for similar sources "+Arrays.toString(sources)+" (expected: "+annotationHash+")");
            }
        }
    }
}
