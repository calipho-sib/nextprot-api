package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 *
 * Created by fnikitin on 02/08/16.
 */
public interface SimilarityCriteria {

    boolean isSimilar(Annotation annotation1, Annotation annotation2);

    static boolean isSimilarObjects(Object o1, Object o2) {

        if (o1 == null && o2 == null)
            return true;
        else if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }
}