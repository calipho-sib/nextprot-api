package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.EnumSet;

/**
 * A variation located on an isoform
 *
 * Created by fnikitin on 05/05/15.
 */
class IsoformVariationPeffFormatter extends IsoformAnnotationPeffFormatter {

    public IsoformVariationPeffFormatter() {

        super(EnumSet.of(AnnotationApiModel.VARIANT), PeffKey.VARIANT);
    }

    @Override
    public String asPeffValue(Isoform isoform, Annotation... annotations) {

        StringBuilder sb = new StringBuilder();

        for (Annotation annotation : annotations) {

            sb.append("(").append(annotation.getStartPositionForIsoform(isoform.getUniqueName()))
                    .append("|").append(annotation.getEndPositionForIsoform(isoform.getUniqueName())).append("|")
                    .append(annotation.getVariant().getVariant()).append(")");
        }

        return sb.toString();
    }

    /**
     * Get all variants of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
     *
     * @param entry the entry to find variant from
     * @param isoform the isoform to find variant of
     * @return a list of Annotation of type VARIANT as PEFF format
     */
    /*public static String getVariantsAsPeffString(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        StringBuilder sb = new StringBuilder();

        for (IsoformVariationPeffFormatter isoformVariation : getListVariant(entry, isoform)) {

            sb.append(isoformVariation.asPeff());
        }

        return sb.toString();
    }

    static List<IsoformVariationPeffFormatter> getListVariant(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        List<IsoformVariationPeffFormatter> isoformVariations = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            if (annotation.getAPICategory() == AnnotationApiModel.VARIANT)
                isoformVariations.add(new IsoformVariationPeffFormatter(isoform.getUniqueName(), annotation));
        }

        Collections.sort(isoformVariations);

        return isoformVariations;
    }*/
}