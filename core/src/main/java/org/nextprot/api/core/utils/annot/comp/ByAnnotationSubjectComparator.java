package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

class ByAnnotationSubjectComparator extends HashableAnnotationComparator {

    ByAnnotationSubjectComparator(final Map<String, Annotation> annotationByHash) {

        this(annotationByHash, new SubjectComparator());
    }

    ByAnnotationSubjectComparator(final Map<String, Annotation> annotationByHash, final Comparator<Annotation> subjectAnnotationComparator) {

        super(annotationByHash, subjectAnnotationComparator);
    }

    @Override
    protected Object getReferencedAnnotationContainer(Annotation annotation) {

        return annotation.getSubjectComponents();
    }

    @Override
    protected String getAnnotationHash(final Annotation annotation) {

        List<String> subjectAnnotationHashList = annotation.getSubjectComponents();

        if (subjectAnnotationHashList.isEmpty())
            throw new NextProtException("cannot find subject for annotation "+ AnnotationUtils.toString(annotation));

        return subjectAnnotationHashList.get(0);
    }

    private static class SubjectComparator implements Comparator<Annotation> {

        @Override
        public int compare(final Annotation sa1, final Annotation sa2) {

            if (sa1.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(sa1));

            if (sa2.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(sa2));

            if (sa1.getAPICategory() == sa2.getAPICategory()) {

                return AnnotationComparators.newComparator(sa1.getAPICategory())
                        .compare(sa1, sa2);
            } else if (sa1.getAPICategory().getParent() == AnnotationCategory.POSITIONAL_ANNOTATION &&
                    sa2.getAPICategory().getParent() == AnnotationCategory.POSITIONAL_ANNOTATION) {

                return new ByFeaturePositionComparator()
                        .compare(sa1, sa2);
            }

            return sa1.getAPICategory().compareTo(sa2.getAPICategory());
        }
    }
}
