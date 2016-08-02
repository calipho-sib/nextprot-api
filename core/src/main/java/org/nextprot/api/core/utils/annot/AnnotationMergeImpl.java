package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.stream.Collectors;

public class AnnotationMergeImpl implements AnnotationMerger {

    @Override
    public void update(Annotation target, Annotation source) {

        updateEvidences(target, source);
        updateQualityQualifier(target);
    }

    private void updateEvidences(Annotation target, Annotation source) {

        // add only different evidences
        target.getEvidences().addAll(source.getEvidences().stream().filter(e -> !target.getEvidences().contains(e)).collect(Collectors.toList()));
    }

    private void updateQualityQualifier(Annotation target) {

        // reset to gold if there is at least one gold evidence
        if (target.getEvidences().stream().anyMatch(e -> QualityQualifier.valueOf(e.getQualityQualifier()) == QualityQualifier.GOLD))
            target.setQualityQualifier(QualityQualifier.GOLD.name());
    }
}
