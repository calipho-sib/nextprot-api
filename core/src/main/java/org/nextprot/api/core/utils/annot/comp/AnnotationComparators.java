package org.nextprot.api.core.utils.annot.comp;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.IsoformUtils;

import java.util.Comparator;

public class AnnotationComparators {

    public static Comparator<Annotation> newComparator(AnnotationCategory annotationCategory, Entry entry) {

        switch (annotationCategory) {
            case MUTAGENESIS:
            case VARIANT:
                return new ByAnnotationVariantComparator(entry);
            case PHENOTYPIC_VARIATION:
                return new ByAnnotationSubjectComparator(entry)
                        .thenComparing(Annotation::getDescription);
            default:
                return new ByIsoformPositionComparator(IsoformUtils.getCanonicalIsoform(entry))
                        .thenComparing(Annotation::getAnnotationId);
        }
    }
}
