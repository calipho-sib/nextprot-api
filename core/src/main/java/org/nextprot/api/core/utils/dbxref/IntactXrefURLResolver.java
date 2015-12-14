package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class IntactXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateUrl(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("EBI")) {
            return CvDatabasePreferredLink.INTACT_BINARY.getLink();
        }

        return super.getTemplateUrl(xref);
    }
}