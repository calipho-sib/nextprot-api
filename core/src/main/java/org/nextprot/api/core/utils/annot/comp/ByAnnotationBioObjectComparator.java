package org.nextprot.api.core.utils.annot.comp;


import java.util.Comparator;
import java.util.Map;

import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.NullableComparable;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;

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

    	String annotHash = annotation.getBioObject().getAnnotationHash();
    	if(annotHash == null){
            throw new NextProtException("no hash for annotation "+ AnnotationUtils.toString(annotation));
    	}
    	return annotHash;
    }

    private static class BioObjectComparator implements Comparator<Annotation> {

        private final NullableComparable<String> nullableComparable = new NullableComparable<>();

        @Override
        public int compare(final Annotation a1, final Annotation a2) {

            if (a1.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(a1));

            if (a2.getAPICategory() == null)
                throw new NextProtException("undefined AnnotationCategory for subject annotation:\n" + AnnotationUtils.toString(a2));

            int cmp = a1.getAPICategory().getApiTypeName().compareTo(a2.getAPICategory().getApiTypeName());

            if (cmp == 0) {

                return nullableComparable.compareNullables(a1.getCvTermName(), a2.getCvTermName());
            }

            return cmp;
        }
    }
}
