package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.impl.FeaturePositionMatcher;
import org.nextprot.api.core.service.annotation.merge.impl.ObjectSimilarityPredicate;
import org.nextprot.api.core.service.annotation.merge.impl.SimilarityPredicateAlternative;
import org.nextprot.api.core.service.annotation.merge.impl.SimilarityPredicateConjunctive;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the contract to evaluate similarity between 2 annotations
 *
 * Created by fnikitin on 02/08/16.
 */
public interface AnnotationSimilarityPredicate {

    /**
     * @return true if annotations are similar else false
     */
    boolean isSimilar(Annotation annotation1, Annotation annotation2);

    /**
     * Static factory method that return a default predicate specific of the given category
     * @param category the annotation category to estimate similarity
     */
    static AnnotationSimilarityPredicate newSimilarityPredicate(AnnotationCategory category) {

        Preconditions.checkNotNull(category);

        List<AnnotationSimilarityPredicate> alternativePredicates = new ArrayList<>();
        alternativePredicates.add((a1, a2) -> a1 == a2);

        List<AnnotationSimilarityPredicate> conjunctivePredicates = new ArrayList<>();
        conjunctivePredicates.add((a1, a2) -> a1.getAPICategory() == a2.getAPICategory());

        switch (category) {
            case GO_BIOLOGICAL_PROCESS:
            case GO_CELLULAR_COMPONENT:
            case GO_MOLECULAR_FUNCTION:
                // TODO: there is a 'is_negative' field to consider in the future
                conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getCvTermAccessionCode));
                break;
            case VARIANT:
            case MUTAGENESIS:
                conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getVariant, (v1, v2) -> v1.getOriginal().equals(v2.getOriginal()) && v1.getVariant().equals(v2.getVariant())));
                conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getTargetingIsoformsMap, new FeaturePositionMatcher()));
                break;
            case GLYCOSYLATION_SITE:
            case MODIFIED_RESIDUE:
                conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getCvTermAccessionCode, (cv1, cv2) -> cv1.equals(cv2)));
                conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getTargetingIsoformsMap, new FeaturePositionMatcher()));
                break;
            case BINARY_INTERACTION:
            case SMALL_MOLECULE_INTERACTION:
                conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getBioObject, (bo1, bo2) -> bo1.getAccession().equals(bo2.getAccession()) && bo1.getDatabase().equalsIgnoreCase(bo2.getDatabase())));
                break;
            default:
                return (a1, a2) -> false;
        }

        alternativePredicates.add(new SimilarityPredicateConjunctive(conjunctivePredicates));

        return new SimilarityPredicateAlternative(alternativePredicates);
    }
}