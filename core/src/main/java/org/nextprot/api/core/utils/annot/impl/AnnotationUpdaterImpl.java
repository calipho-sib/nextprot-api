package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.utils.annot.AnnotationUpdater;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationUpdaterImpl implements AnnotationUpdater {

    @Override
    public void update(Annotation target, Annotation source) {

        updateEvidences(target, source);
        updateQualityQualifier(target);
        updateAnnotationHash(target, source);
    }

    private void updateEvidences(Annotation target, Annotation source) {

        List<AnnotationEvidence> all = new ArrayList<>(target.getEvidences());

        // add only different evidences
        all.addAll(source.getEvidences().stream().filter(e -> !target.getEvidences().contains(e)).collect(Collectors.toList()));

        target.setEvidences(all);
    }

    private void updateQualityQualifier(Annotation target) {

        // reset to gold if there is at least one gold evidence
        if (target.getEvidences().stream().anyMatch(e -> QualityQualifier.valueOf(e.getQualityQualifier()) == QualityQualifier.GOLD))
            target.setQualityQualifier(QualityQualifier.GOLD.name());
    }

    /** @throws NextProtException if annotation hash was not defined in source */
    private void updateAnnotationHash(Annotation target, Annotation source) {

        String annotationHash = source.getAnnotationHash();

        if (annotationHash == null || annotationHash.isEmpty())
            throw new NextProtException("annotation hash was not computed for source "+source.getAnnotationName());

        target.setAnnotationHash(annotationHash);
    }
}
