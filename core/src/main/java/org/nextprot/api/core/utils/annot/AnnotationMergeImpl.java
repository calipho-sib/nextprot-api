package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.List;

public class AnnotationMergeImpl implements AnnotationMerger {

    @Override
    public void update(Annotation target, Annotation source) {

        // add only different evidences
        target.getEvidences().addAll(source.getEvidences());

        if (hasAtLeastOneGoldEvidence(target.getEvidences()))
            target.setQualityQualifier(QualityQualifier.GOLD.name());
    }

    private boolean hasAtLeastOneGoldEvidence(List<AnnotationEvidence> evidences) {

        for (AnnotationEvidence evidence : evidences) {

            if (QualityQualifier.valueOf(evidence.getQualityQualifier()) == QualityQualifier.GOLD)
                return true;
        }

        return false;
    }
}
