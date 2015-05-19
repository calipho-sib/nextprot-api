package org.nextprot.api.core.utils.peff;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;

import java.util.*;
import java.util.logging.Logger;

/**
 * A modification located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public abstract class IsoformPTM extends IsoformAnnotation {

    private static Logger LOGGER = Logger.getLogger("Modification");
    private final String modName;

    protected IsoformPTM(String isoformId, Annotation annotation, Set<AnnotationApiModel> supportedApiModel, String modName) {

        super(isoformId, annotation, supportedApiModel);

        this.modName = modName;
    }

    public static IsoformPTM valueOf(String isoformId, Annotation annotation) {

        AnnotationApiModel apiModel = annotation.getAPICategory();

        if (DisulfideBond.isModelSupported(apiModel))
            return new DisulfideBond(isoformId, annotation);
        else if (IsoformPTMNoPsi.isModelSupported(apiModel))
            return new IsoformPTMNoPsi(isoformId, annotation);
        else if (IsoformPTMPsi.isModelSupported(apiModel))
            return new IsoformPTMPsi(isoformId, annotation);
        else
            LOGGER.warning("could not create instance of annotation of type "+annotation.getAPICategory()+" of isoform id "+isoformId);

        return null;
    }

    public abstract boolean isPSI();

    public String getModificationName() {
        return modName;
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(modName).append(")");
        return sb.toString();
    }

    /**
     * Get all PSI modifications of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
     *
     * @param entry the entry to find modified residues from
     * @param isoform the isoform to find modification
     * @return a list of Annotation of type MODIFICATIONS as PEFF format
     */
    public static String getPsiPTMsAsPeffString(Entry entry, Isoform isoform) {

        return getGenericPTMsAsPeffString(entry, isoform, true);
    }

    /**
     * Get all NO PSI modifications of a given isoform as string specified in PEFF developed by the HUPO PSI (PubMed:19132688)
     *
     * @param entry the entry to find modified residues from
     * @param isoform the isoform to find modification
     * @return a list of Annotation of type MODIFICATIONS as PEFF format
     */
    public static String getNoPsiPTMsAsPeffString(Entry entry, Isoform isoform) {

        return getGenericPTMsAsPeffString(entry, isoform, false);
    }

    /**
     * Get all modifications of given isoform (Kind considered are SELENOCYSTEINE, LIPIDATION_SITE, GLYCOSYLATION_SITE,
     * CROSS_LINK. DISULFIDE_BOND, MODIFIED_RESIDUE and PTM_INFO)
     *
     * @param entry
     * @param isoform
     * @return
     */
    static List<IsoformPTM> getListGenericPTM(Entry entry, Isoform isoform) {

        Preconditions.checkNotNull(entry);

        List<IsoformPTM> isoformPTMs = new ArrayList<>();

        for (Annotation annotation : entry.getAnnotationsByIsoform(isoform.getUniqueName())) {

            if (annotation.getAPICategory().isChildOf(AnnotationApiModel.GENERIC_PTM) && annotation.getAPICategory() != AnnotationApiModel.PTM_INFO) {

                isoformPTMs.add(IsoformPTM.valueOf(isoform.getUniqueName(), annotation));
            }
        }

        Collections.sort(isoformPTMs);

        return isoformPTMs;
    }

    static String getGenericPTMsAsPeffString(Entry entry, Isoform isoform, boolean fetchPsi) {

        Preconditions.checkNotNull(entry);

        StringBuilder sb = new StringBuilder();

        for (IsoformPTM modif : IsoformPTM.getListGenericPTM(entry, isoform)) {

            if (modif.isPSI() == fetchPsi)
                sb.append(modif.asPeff());
        }

        return sb.toString();
    }
}
