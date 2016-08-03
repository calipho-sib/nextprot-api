
package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Base class for
 * Created by fnikitin on 02/08/16.
 */
public class SimilarityCriteriaImpl implements SimilarityCriteria {

    private final ObjectAccessor accessor;

    public SimilarityCriteriaImpl(ObjectAccessor accessor) {

        this.accessor = accessor;
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return (annotation1.getAPICategory() == annotation2.getAPICategory()) && match(annotation1, annotation2);
    }

    protected boolean match(Annotation annotation1, Annotation annotation2) {

        return SimilarityCriteriaImpl.matchObjects(accessor.getObjectToMatch(annotation1),
                accessor.getObjectToMatch(annotation2));
    }

    private static boolean matchObjects(Object o1, Object o2) {

        if (o1 == null && o2 == null)
            return true;
        else if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

    interface ObjectAccessor {

        Object getObjectToMatch(Annotation annotation);
    }
}