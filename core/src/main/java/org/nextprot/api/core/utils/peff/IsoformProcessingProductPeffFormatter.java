package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fnikitin on 05/05/15.
 */
class IsoformProcessingProductPeffFormatter extends IsoformAnnotationPeffFormatter {

    private static final Map<AnnotationCategory, String> PSI_PEFF_MAP;

    static {

        PSI_PEFF_MAP = new HashMap<>();

        PSI_PEFF_MAP.put(AnnotationCategory.SIGNAL_PEPTIDE, "SIGNAL");
        PSI_PEFF_MAP.put(AnnotationCategory.MATURATION_PEPTIDE, "PROPEP");
        PSI_PEFF_MAP.put(AnnotationCategory.MATURE_PROTEIN, "CHAIN");
        PSI_PEFF_MAP.put(AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE, "TRANSIT");
        PSI_PEFF_MAP.put(AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE, "TRANSIT");
    }

    IsoformProcessingProductPeffFormatter() {

        super(PSI_PEFF_MAP.keySet(), PeffKey.PROCESSED);
    }

    @Override
    public String asPeffValue(Isoform isoform, Annotation... annotations) {

        StringBuilder sb = new StringBuilder("");

        for (Annotation annotation : annotations) {

            if (support(annotation))
                sb.append("(").append(annotation.getStartPositionForIsoform(isoform.getUniqueName()))
                    .append("|").append(annotation.getEndPositionForIsoform(isoform.getUniqueName()))
                    .append("|").append(PSI_PEFF_MAP.get(annotation.getAPICategory())).append(")");
        }

        return sb.toString();
    }
}
