package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ByTargetIsoformPositionCriteria extends SimilarityCriteriaImpl {

    @Override
    public boolean match(Annotation annotation1, Annotation annotation2) {

        return SimilarityCriteriaImpl.matchObjects(annotation1.getTargetingIsoformsMap(), annotation2.getTargetingIsoformsMap());
    }
}
