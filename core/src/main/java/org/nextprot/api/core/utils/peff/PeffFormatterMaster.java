package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;

/**
 * Created by fnikitin on 04/06/15.
 */
public class PeffFormatterMaster {

    private final Map<AnnotationApiModel, PeffFormatter> formatterByAnnotationType;
    private final Map<PeffFormatter, List<Annotation>> annotationsByFormatter;
    private final List<PeffFormatter> formatterList;

    public PeffFormatterMaster() {

        this(new IsoformPTMNoPsiPeffFormatter(),
                new DisulfideBondPeffFormatter(),
                new IsoformPTMPsiPeffFormatter(),
                new IsoformVariationPeffFormatter(),
                new IsoformProcessingProductPeffFormatter());
    }

    /**
     * Create a new instance given an array of formatters that do the real job.
     *
     * @param formatters an array of delegated formatters (the order of peff key/value is defined by order of formatters)
     */
    private PeffFormatterMaster(IsoformAnnotationPeffFormatter... formatters) {

        formatterList = new ArrayList<>();
        formatterByAnnotationType = new HashMap<>();
        annotationsByFormatter = new HashMap<>();

        for (IsoformAnnotationPeffFormatter formatter : formatters) {

            for (AnnotationApiModel model : formatter.getSupportedApiModels()) {

                formatterByAnnotationType.put(model, formatter);
            }

            formatterList.add(formatter);
            annotationsByFormatter.put(formatter, new ArrayList<Annotation>());
        }
    }

    private PeffFormatter getFormatter(Annotation annotation) {

        Preconditions.checkArgument(formatterByAnnotationType.containsKey(annotation.getAPICategory()));

        return formatterByAnnotationType.get(annotation.getAPICategory());
    }

    private void clearAnnotations() {

        for (PeffFormatter formatter : annotationsByFormatter.keySet()) {

            annotationsByFormatter.get(formatter).clear();
        }
    }

    public String format(Entry entry, final Isoform isoform) {

        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(isoform);

        categorizeAnnotations(entry, isoform);

        StringBuilder sb = new StringBuilder();

        for (PeffFormatter formatter : formatterList) {

            // make the formatting only in presence of annotations
            if (!annotationsByFormatter.get(formatter).isEmpty()) {

                List<Annotation> annotations = annotationsByFormatter.get(formatter);

                PeffFormatter.PeffKey peffKey = formatter.getPeffKey();
                sb.append("\\").append(peffKey.getName()).append("=");

                Collections.sort(annotations, new Comparator<Annotation>() {
                    @Override
                    public int compare(Annotation a1, Annotation a2) {

                        return Integer.compare(a1.getStartPositionForIsoform(isoform.getUniqueName()), a2.getStartPositionForIsoform(isoform.getUniqueName()));
                    }
                });

                sb.append(formatter.asPeffValue(isoform, annotations.toArray(new Annotation[annotations.size()])));
                sb.append(" ");
            }
        }

        if (sb.length()>0) sb.delete(sb.length()-1, sb.length());

        return sb.toString();
    }

    private void categorizeAnnotations(Entry entry, Isoform isoform) {

        clearAnnotations();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            List<Annotation> annots = annotationsByFormatter.get(getFormatter(annotation));

            annots.add(annotation);
        }
    }
}
