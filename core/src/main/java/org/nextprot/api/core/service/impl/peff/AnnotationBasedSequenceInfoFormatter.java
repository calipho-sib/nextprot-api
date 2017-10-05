package org.nextprot.api.core.service.impl.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.utils.NullableComparable;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.*;


/**
 * Annotation located on isoform formattable in PEFF specified by the HUPO PSI (PubMed:19132688)
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class AnnotationBasedSequenceInfoFormatter extends SequenceInfoFormat {

    private static final Map<AnnotationCategory, SequenceInfoFormat> map = new HashMap<>();

    private final Set<AnnotationCategory> supportedApiModels;
    protected final Entry entry;
    protected final String isoformAccession;

    AnnotationBasedSequenceInfoFormatter(Entry entry, String isoformAccession,
                                         Set<AnnotationCategory> supportedApiModels,
                                         SequenceDescriptorKey SequenceDescriptorKey) {

        super(SequenceDescriptorKey);

        Preconditions.checkNotNull(supportedApiModels);
        Preconditions.checkNotNull(SequenceDescriptorKey);

        this.entry = entry;
        this.isoformAccession = isoformAccession;
        this.supportedApiModels = supportedApiModels;

        for (AnnotationCategory model : supportedApiModels) {

            map.put(model, this);
        }
    }

    protected boolean doHandleAnnotation(Annotation annotation) {

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

        StringBuilder sb = new StringBuilder("");

        List<Annotation> annots = selectAnnotation();
        annots.sort(createAnnotationComparator(isoformAccession));

        for (Annotation annotation : annots) {

            formatAnnotation(annotation, sb);
        }

        return sb.toString();
    }

    protected List<Annotation> selectAnnotation() {

        List<Annotation> annots = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoformAccession)) {

            if (doHandleAnnotation(annotation)) {

                annots.add(annotation);
            }
        }

        return annots;
    }
}
