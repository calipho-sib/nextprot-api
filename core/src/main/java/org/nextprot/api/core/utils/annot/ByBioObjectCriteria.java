package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ByBioObjectCriteria extends SimilarityCriteriaImpl {

    @Override
    protected boolean match(Annotation annotation1, Annotation annotation2) {

        return SimilarityCriteriaImpl.matchObjects(annotation1.getBioObject(), annotation2.getBioObject());
    }
}
