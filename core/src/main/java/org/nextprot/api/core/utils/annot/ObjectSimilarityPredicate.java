
package org.nextprot.api.core.utils.annot;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * Similarity predicate implementation based on object accessible from annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class ObjectSimilarityPredicate implements SimilarityPredicate {

    private final ObjectAccessor accessor;

    public ObjectSimilarityPredicate(ObjectAccessor accessor) {

        this.accessor = accessor;
    }

    public Object getObject(Annotation annotation) {

        return accessor.getObject(annotation);
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return annotation1 == annotation2 || (annotation1.getAPICategory() == annotation2.getAPICategory() && match(annotation1, annotation2));
    }

    /**
     * Default implementation of matching objects accessible from annotation (based on equals()).
     * Extend it if needed.
     *
     * @return true if both objects matches else false
     */
    protected boolean match(Annotation annotation1, Annotation annotation2) {

        return ObjectSimilarityPredicate.equalObjects(accessor.getObject(annotation1),
                accessor.getObject(annotation2));
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
     * Define contract to access an object contained in an annotation
     */
    interface ObjectAccessor {

        /**
         * @return object accessible from annotation (can be null)
         */
        Object getObject(Annotation annotation);
    }
}