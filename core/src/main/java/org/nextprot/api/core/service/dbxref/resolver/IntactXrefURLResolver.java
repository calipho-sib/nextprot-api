package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class IntactXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("EBI")) {
            return CvDatabasePreferredLink.INTACT_BINARY.getLink();
        }

        return super.getTemplateURL(xref);
    }
}