package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class ByAnnotationSubjectComparator implements HashableAnnotationComparator {

    private final Map<String, Annotation> annotationByHash;
    private final Comparator<Annotation> subjectAnnotationComparator;

    ByAnnotationSubjectComparator(final Map<String, Annotation> annotationByHash) {

        this(new SubjectComparator(annotationByHash));
    }

    /**
     * Construct an instance of ByAnnotationSubjectComparator with a given map of referenced annotations
     */
    ByAnnotationSubjectComparator(HashableAnnotationComparator subjectAnnotationComparator) {

        this.annotationByHash = subjectAnnotationComparator.getHashableAnnotations();
        this.subjectAnnotationComparator = subjectAnnotationComparator;
    }

    @Override
    public int compare(final Annotation a1, final Annotation a2) {

        if (a1.getSubjectComponents() == null || a2.getSubjectComponents() == null) {

            if (a1.getSubjectComponents() == a2.getSubjectComponents())
                return 0;
            else if (a1.getSubjectComponents() == null)
                return 1;
            else
                return -1;
        }

        return subjectAnnotationComparator.compare(getFirstSubjectAnnotation(a1), getFirstSubjectAnnotation(a2));
    }

    private Annotation getFirstSubjectAnnotation(final Annotation a) {

        List<Annotation> subjectAnnotationList = a.getSubjectComponents().stream()
                .map(annotationByHash::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (subjectAnnotationList.isEmpty())
            throw new NextProtException("cannot find subject for annotation "+ AnnotationUtils.toString(a));

        return subjectAnnotationList.get(0);
    }

    @Override
    public Map<String, Annotation> getHashableAnnotations() {

        return annotationByHash;
    }

    private static class SubjectComparator implements HashableAnnotationComparator {

        private final Map<String, Annotation> annotationByHash;

        SubjectComparator(final Map<String, Annotation> annotationByHash) {

            this.annotationByHash = annotationByHash;
        }

        @Override
        public int compare(final Annotation sa1, final Annotation sa2) {

            if (sa1.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(sa1));

            if (sa2.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(sa2));

            if (sa1.getAPICategory() == sa2.getAPICategory()) {

                return AnnotationComparators.newComparator(sa1.getAPICategory(), annotationByHash)
                        .compare(sa1, sa2);
            } else if (sa1.getAPICategory().getParent() == AnnotationCategory.POSITIONAL_ANNOTATION &&
                    sa2.getAPICategory().getParent() == AnnotationCategory.POSITIONAL_ANNOTATION) {

                return new ByFeaturePositionComparator()
                        .compare(sa1, sa2);
            }

            return sa1.getAPICategory().compareTo(sa2.getAPICategory());
        }

        @Override
        public Map<String, Annotation> getHashableAnnotations() {

            return annotationByHash;
        }
    }
}
