package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ByTargetIsoformPositionCriteria implements SimilarityCriteria {

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return SimilarityCriteria.isSimilarObjects(annotation1.getTargetingIsoformsMap(), annotation2.getTargetingIsoformsMap());
    }
}
