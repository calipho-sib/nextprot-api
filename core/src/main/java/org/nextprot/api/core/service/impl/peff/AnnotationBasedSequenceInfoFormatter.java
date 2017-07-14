package org.nextprot.api.core.service.impl.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

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

    protected boolean doHandleAnnotation(Annotation annotation, String isoformAccession) {

        return supportedApiModels.contains(annotation.getAPICategory());
    }

    protected abstract void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb);

    protected Comparator<Annotation> createAnnotationComparator(String isoformAccession) {

        return Comparator.comparingInt(a -> a.getStartPositionForIsoform(isoformAccession));
    }

    @Override
    protected String formatValue(Entry entry, String isoformAccession) {

        StringBuilder sb = new StringBuilder("");

        for (Annotation annotation : extractAnnotation(entry, isoformAccession)) {

            formatAnnotation(isoformAccession, annotation, sb);
        }

        return sb.toString();
    }

    private List<Annotation> extractAnnotation(Entry entry, String isoformAccession) {

        List<Annotation> annots = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoformAccession)) {

            if (doHandleAnnotation(annotation, isoformAccession)) {

                annots.add(annotation);
            }
        }

        annots.sort(createAnnotationComparator(isoformAccession));

        return annots;
    }
}
