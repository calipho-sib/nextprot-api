package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

// http://www.ontobee.org/
class OboLibraryXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.contains(":")) {

            return accession.replaceFirst(":", "_");
        }

        throw new UnresolvedXrefURLException("':' is missing in accession number '"+accession+"'");
    }

    @Override
    protected String getTemplateUrl(DbXref xref) {

        return CvDatabasePreferredLink.OBO.getLink();
    }

}