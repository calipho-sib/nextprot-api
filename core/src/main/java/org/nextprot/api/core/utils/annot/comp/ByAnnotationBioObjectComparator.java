package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.BioGenericObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

import java.util.Comparator;
import java.util.Map;

import static org.nextprot.api.core.utils.annot.comp.AnnotationComparators.compareNullableComparableObject;

class ByAnnotationBioObjectComparator extends HashableAnnotationComparator {

    ByAnnotationBioObjectComparator(final Map<String, Annotation> annotationByHash) {

        this(annotationByHash, new ByAnnotationBioObjectComparator.BioObjectComparator());
    }

    ByAnnotationBioObjectComparator(final Map<String, Annotation> annotationByHash, final Comparator<Annotation> subjectAnnotationComparator) {

        super(annotationByHash, subjectAnnotationComparator);
    }

    @Override
    protected Object getReferencedAnnotationContainer(Annotation annotation) {

        return annotation.getBioObject();
    }

    @Override
    protected String getAnnotationHash(Annotation annotation) {

        if (! (annotation.getBioObject() instanceof BioGenericObject) )
            throw new NextProtException("no hash for annotation "+ AnnotationUtils.toString(annotation));

        BioGenericObject bgo = (BioGenericObject) annotation.getBioObject();

        return bgo.getAnnotationHash();
    }

    private static class BioObjectComparator implements Comparator<Annotation> {

        @Override
        public int compare(final Annotation a1, final Annotation a2) {

            if (a1.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(a1));

            if (a2.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(a2));

            int cmp = a1.getAPICategory().getApiTypeName().compareTo(a2.getAPICategory().getApiTypeName());

            if (cmp == 0) {

                return compareNullableComparableObject(a1.getCvTermName(), a2.getCvTermName());
            }

            return cmp;
        }
    }
}
