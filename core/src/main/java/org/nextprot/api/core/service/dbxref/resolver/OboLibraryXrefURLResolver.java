package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

// http://www.ontobee.org/
class OboLibraryXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.contains(":")) {

            return accession.replaceFirst(":", "_");
        }

        throw new UnresolvedXrefURLException("':' is missing in accession number '"+accession+"'");
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        return CvDatabasePreferredLink.OBO.getLink();
    }

}