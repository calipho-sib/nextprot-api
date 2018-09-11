package org.nextprot.api.core.service.impl.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.NullableComparable;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;


/**
 * Annotation located on isoform formattable in PEFF specified by the HUPO PSI (PubMed:19132688)
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class AnnotationBasedPEFFInformation extends PEFFInformation {

    private static final Map<AnnotationCategory, PEFFInformation> map = new HashMap<>();

    private final Set<AnnotationCategory> supportedApiModels;
    protected final String isoformAccession;
    private final List<Annotation> isoformAnnotations;

    AnnotationBasedPEFFInformation(String isoformAccession, List<Annotation> isoformAnnotations,
                                   Set<AnnotationCategory> supportedApiModels,
                                   Key Key) {

        super(Key);

        Preconditions.checkNotNull(supportedApiModels);
        Preconditions.checkNotNull(Key);

        this.isoformAccession = isoformAccession;
        this.isoformAnnotations = isoformAnnotations;
        this.supportedApiModels = supportedApiModels;

        for (AnnotationCategory model : supportedApiModels) {

            map.put(model, this);
        }
    }

    protected boolean selectAnnotation(Annotation annotation) {

        return supportedApiModels.contains(annotation.getAPICategory());
    }

    protected abstract void formatAnnotation(Annotation annotation, StringBuilder sb);

    protected Comparator<Annotation> createAnnotationComparator(String isoformAccession) {

        NullableComparable<Integer> nullableComparable = new NullableComparable<>();

        return (a1, a2) -> {

            int cmp = nullableComparable.compareNullables(a1.getStartPositionForIsoform(isoformAccession), a2.getStartPositionForIsoform(isoformAccession), false);

            if (cmp == 0) {
                return nullableComparable.compareNullables(a1.getEndPositionForIsoform(isoformAccession), a2.getEndPositionForIsoform(isoformAccession), false);
            }
            return cmp;
        };
    }

    @Override
    protected String formatValue() {

        StringBuilder sb = new StringBuilder();

        List<Annotation> annots = filterAnnotation(isoformAccession);
        annots.sort(createAnnotationComparator(isoformAccession));

        for (Annotation annotation : annots) {

            formatAnnotation(annotation, sb);
        }

        return sb.toString();
    }

    protected List<Annotation> filterAnnotation(String isoformAccession) {

        List<Annotation> annots = new ArrayList<>();

        for (Annotation annotation : isoformAnnotations) {

            if (selectAnnotation(annotation)) {

                annots.add(annotation);
            }
        }

        return annots;
    }

    boolean isPositional(Annotation annotation) {

        return annotation.getStartPositionForIsoform(isoformAccession) != null &&
                annotation.getEndPositionForIsoform(isoformAccession) != null;
    }
}
