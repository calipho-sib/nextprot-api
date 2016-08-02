package org.nextprot.api.core.utils.annot;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Collection;

/**
 * Find annotation in collection of annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public abstract class AnnotationFinder {

    public static AnnotationFinder newFinder(String category) {

        return newFinder(AnnotationCategory.getByDbAnnotationTypeName(category));
    }

    public static AnnotationFinder newFinder(AnnotationCategory category) {

        switch (category) {
            case GO_BIOLOGICAL_PROCESS:
            case GO_CELLULAR_COMPONENT:
            case GO_MOLECULAR_FUNCTION:
                return new GOFinder();
        }

        throw new NextProtException("\nCould not find annotation finder for " + category);
    }

    /**
     * @return the annotation found from a list of annotations else null
     */
    public Annotation find(Annotation annotation, Collection<Annotation> annotations) {

        for (Annotation annot : annotations) {

            if (match(annotation, annot))
                return annot;
        }

        return null;
    }

    public boolean match(Annotation annotation1, Annotation annotation2) {

        if (annotation1.getCategory().equals(annotation2.getCategory()))
            return isSimilar(annotation1, annotation2);

        return false;
    }

    protected abstract boolean isSimilar(Annotation annotation1, Annotation annotation2);
}
