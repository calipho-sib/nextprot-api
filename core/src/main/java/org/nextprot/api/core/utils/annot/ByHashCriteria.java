package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Find annotations by annotation hash code
 *
 * Created by fnikitin on 02/08/16.
 */
public class ByHashCriteria extends SimilarityCriteriaImpl {

    @Override
    public boolean match(Annotation annotation1, Annotation annotation2) {

        return SimilarityCriteriaImpl.matchObjects(annotation1.getAnnotationHash(), annotation2.getAnnotationHash());
    }
}
