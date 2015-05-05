package org.nextprot.api.core.utils.peff;

import org.nextprot.api.core.domain.annotation.Annotation;

/**
 * A modification located on isoform
 *
 * Created by fnikitin on 05/05/15.
 */
public abstract class Modification extends LocatedAnnotation {

    private final String modName;

    protected Modification(String isoformId, Annotation annotation) {

        super(isoformId, annotation);

        modName = annotation.getCvTermName();
    }

    public static Modification valueOf(String isoformId, Annotation annotation) {

        /**
         * SELENOCYSTEINE, LIPIDATION_SITE, GLYCOSYLATION_SITE,
         * CROSS_LINK. DISULFIDE_BOND, MODIFIED_RESIDUE and PTM_INFO
         */
        switch (annotation.getAPICategory()) {

            case DISULFIDE_BOND:
                return new Disulfide(isoformId, annotation);
            default:
                return new ModifiedResidue(isoformId, annotation);
        }
    }

    public String getModificationName() {
        return modName;
    }

    @Override
    public String asPeff() {

        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getStart()).append("|").append(modName).append(")");
        return sb.toString();
    }
}
