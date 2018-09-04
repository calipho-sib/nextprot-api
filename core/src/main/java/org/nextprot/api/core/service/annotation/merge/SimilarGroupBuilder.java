package org.nextprot.api.core.service.annotation.merge;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;
import java.util.stream.Collectors;

public class SimilarGroupBuilder {

    private final List<Annotation> uniqueAnnotations;

    public SimilarGroupBuilder(List<Annotation> uniqueAnnotations) {

        this.uniqueAnnotations = new ArrayList<>(uniqueAnnotations);
    }

    /**
     * Group similar annotations in cluster
     *
     * @param externalAnnotations supplementary annotations
     * @return a list of clusters
     */
    public List<SimilarAnnotationGroup> groupBySimilarity(List<Annotation> externalAnnotations) {

        List<SimilarAnnotationGroup> similarAnnotationGroups =
                SimilarAnnotationGroup.fromAnnotationList(uniqueAnnotations);

        if (externalAnnotations == null || externalAnnotations.isEmpty()) {

            return similarAnnotationGroups;
        }

        AnnotationGroupFinder finder = new AnnotationGroupFinder();

        for (Annotation externalAnnotation : externalAnnotations) {

            Optional<SimilarAnnotationGroup> foundAnnotationGroup =
                    finder.findSimilarAnnotationGroup(externalAnnotation, similarAnnotationGroups);

            if (foundAnnotationGroup.isPresent()) {
                try {
                    foundAnnotationGroup.get().add(externalAnnotation);
                } catch (SimilarAnnotationGroup.InvalidAnnotationGroupCategoryException e) {
                    throw new NextProtException(e);
                }
            }
            else {
                similarAnnotationGroups.add(new SimilarAnnotationGroup(externalAnnotation));
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

        private final AnnotationFinder annotationFinder = new AnnotationFinder();

        private Optional<SimilarAnnotationGroup> findSimilarAnnotationGroup(Annotation searchedAnnotation, Collection<SimilarAnnotationGroup> similarAnnotationGroups) {

            for (SimilarAnnotationGroup similarAnnotationGroup : similarAnnotationGroups) {
                if (annotationFinder.findAnnotation(searchedAnnotation, similarAnnotationGroup.getAnnotations()).isPresent())
                    return Optional.of(similarAnnotationGroup);
            }

            return Optional.empty();
        }
    }

    public static class AnnotationFinder {

        public Optional<Annotation> findAnnotation(Annotation searchedAnnotation, Collection<Annotation> annotations) {

            AnnotationSimilarityPredicate predicate = newPredicate(searchedAnnotation);

            for (Annotation annotation : annotations) {
                if (predicate.isSimilar(searchedAnnotation, annotation))
                    return Optional.of(annotation);
            }

            return Optional.empty();
        }

        protected AnnotationSimilarityPredicate newPredicate(Annotation annotation) {

            return AnnotationSimilarityPredicate.newSimilarityPredicate(annotation.getAPICategory());
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
