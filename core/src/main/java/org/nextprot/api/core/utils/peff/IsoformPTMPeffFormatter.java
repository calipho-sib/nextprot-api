package org.nextprot.api.core.utils.peff;

import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.Set;

/**
 * A modification located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
abstract class IsoformPTMPeffFormatter extends IsoformAnnotationPeffFormatter {

    protected IsoformPTMPeffFormatter(Set<AnnotationApiModel> supportedApiModel, PeffKey peffKey) {

        super(supportedApiModel, peffKey);
    }

    public abstract boolean isPSI();

    protected abstract String getModName(Annotation annotation);

    @Override
    public String asPeffValue(Isoform isoform, Annotation... annotations) {

        StringBuilder sb = new StringBuilder("");

        for (Annotation annotation : annotations) {

            sb.append("(").append(annotation.getStartPositionForIsoform(isoform.getUniqueName()))
                    .append("|").append(getModName(annotation)).append(")");
        }
        return sb.toString();
    }

    /**
     * Get all PSI modifications of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
     *
     * @param entry the entry to find modified residues from
     * @param isoform the isoform to find modification
     * @return a list of Annotation of type MODIFICATIONS as PEFF format
     */
    /*public static String getPsiPTMsAsPeffString(Entry entry, Isoform isoform) {

        return getGenericPTMsAsPeffString(entry, isoform, true);
    }*/

    /**
     * Get all NO PSI modifications of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
     *
     * @param entry the entry to find modified residues from
     * @param isoform the isoform to find modification
     * @return a list of Annotation of type MODIFICATIONS as PEFF format
     */
    /*public static String getNoPsiPTMsAsPeffString(Entry entry, Isoform isoform) {

        return getGenericPTMsAsPeffString(entry, isoform, false);
    }*/

    /**
     * Get all modifications of given isoform (Kind considered are SELENOCYSTEINE, LIPIDATION_SITE, GLYCOSYLATION_SITE,
     * CROSS_LINK. DISULFIDE_BOND, MODIFIED_RESIDUE and PTM_INFO)
     *
     * @param entry
     * @param isoform
     * @return
     */
    /*static List<IsoformPTMPeffFormatter> getListGenericPTM(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        List<IsoformPTMPeffFormatter> isoformPTMs = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            if (annotation.getAPICategory().isChildOf(AnnotationApiModel.GENERIC_PTM) && annotation.getAPICategory() != AnnotationApiModel.PTM_INFO) {

                isoformPTMs.add(IsoformPTMPeffFormatter.valueOf(isoform.getUniqueName(), annotation));
            }
        }

        Collections.sort(isoformPTMs);

        return isoformPTMs;
    }

    static String getGenericPTMsAsPeffString(Entry entry, Isoform isoform, boolean fetchPsi) {

        Preconditions.checkNotNull(entry);

        StringBuilder sb = new StringBuilder();

        for (IsoformPTMPeffFormatter modif : IsoformPTMPeffFormatter.getListGenericPTM(entry, isoform)) {

            if (modif.isPSI() == fetchPsi)
                sb.append(modif.asPeffValue());
        }

        return sb.toString();
    }*/
}
