package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.HashMap;
import java.util.Map;

class ProcessedMoleculeFormatter extends AnnotationBasedSequenceInfoFormatter {

    private static final Map<AnnotationCategory, String> PSI_PEFF_MAP;

    static {

        PSI_PEFF_MAP = new HashMap<>();

        PSI_PEFF_MAP.put(AnnotationCategory.SIGNAL_PEPTIDE, "SIGNAL");
        PSI_PEFF_MAP.put(AnnotationCategory.MATURATION_PEPTIDE, "PROPEP");
        PSI_PEFF_MAP.put(AnnotationCategory.MATURE_PROTEIN, "CHAIN");
        PSI_PEFF_MAP.put(AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE, "TRANSIT");
        PSI_PEFF_MAP.put(AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE, "TRANSIT");
    }

    ProcessedMoleculeFormatter() {

        super(PSI_PEFF_MAP.keySet(), SequenceDescriptorKey.PROCESSED);
    }

    @Override
    protected void formatAnnotation(String isoformAccession, Annotation annotation, StringBuilder sb) {

        sb
                .append("(")
                .append(annotation.getStartPositionForIsoform(isoformAccession))
                .append("|")
                .append(annotation.getEndPositionForIsoform(isoformAccession))
                .append("|")
                .append(PSI_PEFF_MAP.get(annotation.getAPICategory()))
                .append(")");
    }
}
