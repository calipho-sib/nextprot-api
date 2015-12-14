package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

public class EmblXrefURLResolver extends DbXrefURLBaseResolver {

    private boolean isCDS;

    @Override
    protected void beforeResolution(DbXref xref) {

        if (xref.getAccession().indexOf('.') > 0) {
            isCDS = true;
        }
    }

    @Override
    protected String getPrimaryId(DbXref xref) {

        String primaryId = xref.getAccession();

        if (isCDS) {
            primaryId = primaryId.split("\\.")[0];
        }

        return primaryId;
    }

    @Override
    protected String getTemplateUrl(DbXref xref) {

        String templateURL = super.getTemplateUrl(xref);

        if (isCDS) {
            templateURL = CvDatabasePreferredLink.EMBL.getLink();
        }

        return templateURL;
    }
}