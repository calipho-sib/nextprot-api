package org.nextprot.api.core.utils.annot;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Arrays;
import java.util.Collection;

/**
 * Find annotation in collection of annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public class AnnotationFinder implements SimilarityCriteria {

    private final SimilarityCriteria criteria;

    public AnnotationFinder(SimilarityCriteria criteria) {

        Preconditions.checkNotNull(criteria);

        this.criteria = criteria;
    }

    /**
     * @return an instance of AnnotationFinder given a category (by hash criteria by default)
     */
    public static AnnotationFinder valueOf(AnnotationCategory category) {

        switch (category) {
            case GO_BIOLOGICAL_PROCESS:
            case GO_CELLULAR_COMPONENT:
            case GO_MOLECULAR_FUNCTION:
                return new AnnotationFinder(new SimilarityCriteriaImpl(Annotation::getCvTermAccessionCode));
            case VARIANT:
            case MUTAGENESIS:
                // Annot name + CV Term + Position + Description + BioObject
                return new AnnotationFinder(new SimilarityCriteriaList(Arrays.asList(
                        new SimilarityCriteriaImpl(Annotation::getAnnotationName),
                        new SimilarityCriteriaImpl(Annotation::getCvTermAccessionCode),
                        new SimilarityCriteriaImpl(Annotation::getTargetingIsoformsMap),
                        new SimilarityCriteriaImpl(Annotation::getDescription),
                        new SimilarityCriteriaImpl(Annotation::getBioObject)
                )));
            default:
                return new AnnotationFinder(new SimilarityCriteriaImpl(Annotation::getAnnotationHash));
        }
    }

    /**
     * @return the annotation found from a list of annotations else null
     */
    public Annotation find(Annotation searchedAnnotation, Collection<Annotation> annotations) {

        for (Annotation annotation : annotations) {

            if (isSimilar(searchedAnnotation, annotation))
                return annotation;
        }

        return null;
    }

    @Override
    public boolean isSimilar(Annotation annotation1, Annotation annotation2) {

        return criteria.isSimilar(annotation1, annotation2);
    }
}
