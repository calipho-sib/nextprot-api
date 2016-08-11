
package org.nextprot.api.core.utils.annot.merge.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.merge.ObjectAccessor;
import org.nextprot.api.core.utils.annot.merge.ObjectMatcher;
import org.nextprot.api.core.utils.annot.merge.SimilarityPredicate;

/**
 * Similarity predicate implementation based on object accessible from annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class ObjectSimilarityPredicate<T> implements SimilarityPredicate, ObjectAccessor<T> {

    private final ObjectAccessor<T> accessor;
    private final ObjectMatcher<T> matcher;

    /**
     * Default constructor with equals() as matcher
     * @param accessor T-object accessor
     */
    public ObjectSimilarityPredicate(ObjectAccessor<T> accessor) {

        this(accessor, new ObjectEqualMatcher<>());
    }

    public ObjectSimilarityPredicate(ObjectAccessor<T> accessor, ObjectMatcher<T> matcher) {

        Preconditions.checkNotNull(accessor);
        Preconditions.checkNotNull(matcher);

        this.accessor = accessor;
        this.matcher = matcher;
    }

    @Override
    public T getObject(Annotation annotation) {

        return accessor.getObject(annotation);
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return annotation1 == annotation2 || ( annotation1.getAPICategory() == annotation2.getAPICategory() &&
                        matcher.match(accessor.getObject(annotation1), accessor.getObject(annotation2)) );
    }
}