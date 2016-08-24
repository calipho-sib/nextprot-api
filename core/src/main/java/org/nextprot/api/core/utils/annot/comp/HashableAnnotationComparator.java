package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator of Annotations that needs an access to annotations from AnnotationHash
 */
abstract class HashableAnnotationComparator implements Comparator<Annotation> {

    private final Map<String, Annotation> annotationByHash;
    private final Comparator<Annotation> referencedAnnotationComparator;

    /**
     * Construct an instance of ByAnnotationSubjectComparator with a given map of referenced annotations
     */
    protected HashableAnnotationComparator(final Map<String, Annotation> annotationByHash, final Comparator<Annotation> referencedAnnotationComparator) {

        this.annotationByHash = annotationByHash;
        this.referencedAnnotationComparator = referencedAnnotationComparator;
    }

    /** @return object that contains the reference */
    protected abstract Object getReferencedAnnotationContainer(Annotation annotation);

    /** @return the annotation hash reference */
    protected abstract String getAnnotationHash(Annotation annotation);

    @Override
    public int compare(final Annotation a1, final Annotation a2) {

        if (getReferencedAnnotationContainer(a1) == null || getReferencedAnnotationContainer(a2) == null) {

            if (getReferencedAnnotationContainer(a1) == getReferencedAnnotationContainer(a2))
                return 0;
            else if (getReferencedAnnotationContainer(a1) == null)
                return 1;
            else
                return -1;
        }

        return referencedAnnotationComparator.compare(
                getReferencedAnnotation(getAnnotationHash(a1)), getReferencedAnnotation(getAnnotationHash(a2))
        );
    }

    private Annotation getReferencedAnnotation(final String annotationHash) {

        if (annotationHash == null)
            throw new NextProtException("undefined annotation hash");

        if (!annotationByHash.containsKey(annotationHash))
            throw new NextProtException("missing annotation where referenced hash is "+annotationHash);

        return annotationByHash.get(annotationHash);
    }
}
