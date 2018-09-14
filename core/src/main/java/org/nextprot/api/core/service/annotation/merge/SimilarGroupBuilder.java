package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.merge.impl.FeaturePositionMatcher;
import org.nextprot.api.core.service.annotation.merge.impl.ObjectSimilarityPredicate;
import org.nextprot.api.core.service.annotation.merge.impl.SimilarityPredicateAlternative;
import org.nextprot.api.core.service.annotation.merge.impl.SimilarityPredicateConjunctive;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimilarGroupBuilder {

    private final List<Annotation> uniqueAnnotations;
    private final AnnotationGroupFinder groupFinder;

    public SimilarGroupBuilder(List<Annotation> uniqueAnnotations) {

        checkUnicity(uniqueAnnotations);
        this.uniqueAnnotations = new ArrayList<>(uniqueAnnotations);
        this.groupFinder = new AnnotationGroupFinder();
    }

    // TODO: should we check the unicity of annotations ?
    private static void checkUnicity(List<Annotation> uniqueAnnotations) {

    }

    /**
     * Group similar annotations in cluster
     *
     * @param otherAnnotations supplementary annotations
     * @return a list of clusters
     */
    public List<SimilarAnnotationGroup> groupBySimilarity(List<Annotation> otherAnnotations) {

        List<SimilarAnnotationGroup> similarAnnotationGroups =
                SimilarAnnotationGroup.fromAnnotationList(uniqueAnnotations);

        if (otherAnnotations == null || otherAnnotations.isEmpty()) {

            return similarAnnotationGroups;
        }

        for (Annotation otherAnnotation : otherAnnotations) {

            Optional<SimilarAnnotationGroup> foundAnnotationGroup =
                    groupFinder.findSimilarAnnotationGroup(otherAnnotation, similarAnnotationGroups);

            if (foundAnnotationGroup.isPresent()) {
                try {
                    foundAnnotationGroup.get().add(otherAnnotation);
                } catch (SimilarAnnotationGroup.InvalidAnnotationGroupCategoryException e) {
                    throw new NextProtException(e);
                }
            }
            else {
                similarAnnotationGroups.add(new SimilarAnnotationGroup(otherAnnotation));
            }
        }

        return similarAnnotationGroups;
    }

    /**
     * Find cluster containing similar annotations
     *
     * Created by fnikitin on 02/08/16.
     */
    private static class AnnotationGroupFinder {

        private final AnnotationFinder annotationFinder;

        public AnnotationGroupFinder() {
            this.annotationFinder = new AnnotationFinder();
        }

        private Optional<SimilarAnnotationGroup> findSimilarAnnotationGroup(Annotation searchedAnnotation, Collection<SimilarAnnotationGroup> similarAnnotationGroups) {

            for (SimilarAnnotationGroup similarAnnotationGroup : similarAnnotationGroups) {
                if (annotationFinder.findAnnotation(searchedAnnotation, similarAnnotationGroup.getAnnotations()).isPresent())
                    return Optional.of(similarAnnotationGroup);
            }

            return Optional.empty();
        }
    }

    public static class AnnotationFinder {

        private final Function<AnnotationCategory, AnnotationSimilarityPredicate> predicateFunc;

        public AnnotationFinder() {

            this((c) -> newSimilarityPredicate(c));
        }

        public AnnotationFinder(Function<AnnotationCategory, AnnotationSimilarityPredicate> predicateFunc) {

            Preconditions.checkNotNull(predicateFunc);
            this.predicateFunc = predicateFunc;
        }

        public Optional<Annotation> findAnnotation(Annotation searchedAnnotation, Collection<Annotation> annotations) {

            for (Annotation annotation : annotations) {
                if (predicateFunc.apply(searchedAnnotation.getAPICategory()).isSimilar(searchedAnnotation, annotation))
                    return Optional.of(annotation);
            }

            return Optional.empty();
        }

        /**
         * Static factory method that return a default predicate specific of the given category
         * @param category the annotation category to estimate similarity
         */
        static AnnotationSimilarityPredicate newSimilarityPredicate(AnnotationCategory category) {

            Preconditions.checkNotNull(category);

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
                case PTM_INFO:
                    conjunctivePredicates.add(new ObjectSimilarityPredicate<>(Annotation::getDescription, (d1, d2) -> Objects.equals(d1, d2)));
                    break;
                default:
                    return (a1, a2) -> false;
            }

            List<AnnotationSimilarityPredicate> alternativePredicates = new ArrayList<>();
            alternativePredicates.add((a1, a2) -> a1 == a2);
            alternativePredicates.add(new SimilarityPredicateConjunctive(conjunctivePredicates));

            return new SimilarityPredicateAlternative(alternativePredicates);
        }
    }

    /**
     * A group of annotation of the same category
     */
    public static class SimilarAnnotationGroup {

        private final AnnotationCategory category;
        private final List<Annotation> group;

        public SimilarAnnotationGroup(Annotation annotation) {

            Preconditions.checkNotNull(annotation);

            group = new ArrayList<>();
            category = annotation.getAPICategory();
            group.add(annotation);
        }

        public static List<SimilarAnnotationGroup> fromAnnotationList(List<Annotation> annotations) {

            List<SimilarAnnotationGroup> similarAnnotationGroups = new ArrayList<>(annotations.size());
            similarAnnotationGroups.addAll(annotations.stream()
                    .map(SimilarAnnotationGroup::new)
                    .collect(Collectors.toList()));

            return similarAnnotationGroups;
        }

        public boolean add(Annotation annotation) throws InvalidAnnotationGroupCategoryException {

            if (annotation.getAPICategory() != category)
                throw new InvalidAnnotationGroupCategoryException(annotation, category);

            return group.add(annotation);
        }

        public int size() {

            return group.size();
        }

        public boolean isEmpty() {

            return group.isEmpty();
        }

        public List<Annotation> getAnnotations() {

            return Collections.unmodifiableList(group);
        }

        public AnnotationCategory getCategory() {

            return category;
        }

        public static class InvalidAnnotationGroupCategoryException extends Exception {

            public InvalidAnnotationGroupCategoryException(Annotation annotation, AnnotationCategory expectedCategory) {

                super("could not add annotation of different category "+annotation.getAPICategory() + " (expected: "+expectedCategory+")");
            }
        }
    }
}
