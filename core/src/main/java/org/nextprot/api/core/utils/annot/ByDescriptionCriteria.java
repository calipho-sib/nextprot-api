package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

public class ByDescriptionCriteria extends SimilarityCriteriaImpl {

    @Override
    public boolean match(Annotation annotation1, Annotation annotation2) {

        return matchObjects(annotation1.getDescription(), annotation2.getDescription());
    }
}
