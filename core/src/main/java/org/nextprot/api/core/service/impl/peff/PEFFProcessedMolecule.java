package org.nextprot.api.core.service.impl.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PEFFProcessedMolecule extends AnnotationBasedPEFFInformation {

    private static final Map<AnnotationCategory, String> ANNOTATION_CATEGORY_TO_NAME;

    static {

        ANNOTATION_CATEGORY_TO_NAME = new HashMap<>();

        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.SIGNAL_PEPTIDE, "signal peptide");                 // was "SIGNAL"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.MATURATION_PEPTIDE, "propeptide");                 // was "PROPEP"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.MATURE_PROTEIN, "mature protein");                 // was "CHAIN"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE, "transit peptide");    // was "TRANSIT"
        ANNOTATION_CATEGORY_TO_NAME.put(AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE, "transit peptide"); // was "TRANSIT"
    }

    public PEFFProcessedMolecule(String isoformAccession, List<Annotation> isoformAnnotations) {

        super(isoformAccession, isoformAnnotations, ANNOTATION_CATEGORY_TO_NAME.keySet(), Key.PROCESSED);
    }

    @Override
    protected void formatAnnotation(Annotation annotation, StringBuilder sb) {

        Integer start = annotation.getStartPositionForIsoform(isoformAccession);
        Integer end = annotation.getEndPositionForIsoform(isoformAccession);

        sb
                .append("(")
                .append((start != null) ? start : "?")
                .append("|")
                .append((end != null) ? end : "?")
                .append("|")
                .append(ANNOTATION_CATEGORY_TO_NAME.get(annotation.getAPICategory()))
                .append(")")
        ;
    }
}
