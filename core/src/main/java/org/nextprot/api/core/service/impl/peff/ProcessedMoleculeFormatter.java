package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.utils.peff.SequenceDescriptorKey;

import java.util.HashMap;
import java.util.Map;

public class ProcessedMoleculeFormatter extends AnnotationBasedSequenceInfoFormatter {

    private static final Map<AnnotationCategory, String> ANNOTATION_CATEGORY_TO_NAME;

    static {

        ANNOTATION_CATEGORY_TO_NAME = new HashMap<>();

        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.SIGNAL_PEPTIDE, "signal peptide");                 // was "SIGNAL"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.MATURATION_PEPTIDE, "maturation peptide");         // was "PROPEP"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.MATURE_PROTEIN, "mature protein");                 // was "CHAIN"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE, "transit peptide");    // was "TRANSIT"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE, "transit peptide"); // was "TRANSIT"
    }

    public ProcessedMoleculeFormatter() {

        super(ANNOTATION_CATEGORY_TO_NAME.keySet(), SequenceDescriptorKey.PROCESSED);
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append("(")
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getEndPositionForIsoform(isoformAccession))
                .append("|")
                .append(ANNOTATION_CATEGORY_TO_NAME.get(annotation.getAPICategory()))
                .append(")")
        ;
    }
}
