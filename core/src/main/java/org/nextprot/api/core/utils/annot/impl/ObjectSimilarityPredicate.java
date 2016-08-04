
package org.nextprot.api.core.utils.annot.impl;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.ObjectAccessor;
import org.nextprot.api.core.utils.annot.ObjectMatcher;
import org.nextprot.api.core.utils.annot.SimilarityPredicate;

/**
 * Similarity predicate implementation based on object accessible from annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class ObjectSimilarityPredicate implements SimilarityPredicate, ObjectAccessor {

    private final ObjectAccessor accessor;
    private final ObjectMatcher matcher;

    public ObjectSimilarityPredicate(ObjectAccessor accessor) {

        this(accessor, new ObjectEqualMatcher());
    }

    public ObjectSimilarityPredicate(ObjectAccessor accessor, ObjectMatcher matcher) {

        this.accessor = accessor;
        this.matcher = matcher;
    }

    @Override
    public Object getObject(Annotation annotation) {

        return accessor.getObject(annotation);
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return annotation1 == annotation2 || ( annotation1.getAPICategory() == annotation2.getAPICategory() &&
                        matcher.match(accessor.getObject(annotation1), accessor.getObject(annotation2)) );
    }

    /**
     * @return true if o1 and 02 both null or equals (based on Object.equals contract) else false
     */
    public static boolean equalObjects(Object o1, Object o2) {

        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

    /**
     * Default implementation of matching objects accessible from annotation (based on equals()).
     *
     * @return true if both objects matches else false
     */
    public static class ObjectEqualMatcher implements ObjectMatcher {

        @Override
        public boolean match(Object o1, Object o2) {

            return ObjectSimilarityPredicate.equalObjects(o1, o2);
        }
    }
}