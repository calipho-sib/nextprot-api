package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;

/**
 * Annotation located on isoform formattable in PEFF specified by the HUPO PSI (PubMed:19132688)
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class AnnotationBasedSequenceInfoFormatter extends SequenceInfoFormatter {

    private static final Map<AnnotationCategory, SequenceInfoFormatter> map = new HashMap<>();

    private final Set<AnnotationCategory> supportedApiModels;

    AnnotationBasedSequenceInfoFormatter(Set<AnnotationCategory> supportedApiModels, SequenceDescriptorKey SequenceDescriptorKey) {

        super(SequenceDescriptorKey);

        Preconditions.checkNotNull(supportedApiModels);
        Preconditions.checkNotNull(SequenceDescriptorKey);

        this.supportedApiModels = supportedApiModels;

        for (AnnotationCategory model : supportedApiModels) {

            map.put(model, this);
        }
    }

    private boolean doHandleAnnotation(Annotation annotation) {

        return supportedApiModels.contains(annotation.getAPICategory());
    }

    protected abstract void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb);

    @Override
    protected String formatValue(Entry entry, String isoformAccession) {

        StringBuilder sb = new StringBuilder("");

        for (Annotation annotation : extractAnnotation(entry, isoformAccession)) {

            if (doHandleAnnotation(annotation)) {

                sb.append("(");
                formatAnnotation(isoformAccession, annotation, sb);
                sb.append(")");
            }
        }

        return sb.toString();
    }

    private List<Annotation> extractAnnotation(Entry entry, String isoformName) {

        List<Annotation> annots = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoformName)) {

            if (doHandleAnnotation(annotation)) {

                annots.add(annotation);
            }
        }

        annots.sort(Comparator.comparingInt(a -> a.getStartPositionForIsoform(isoformName)));

        return annots;
    }
}
