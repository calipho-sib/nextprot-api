package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

// http://www.ontobee.org/
class OboLibraryXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.contains(":")) {

            return accession.replaceFirst(":", "_");
        }

        throw new UnresolvedXrefURLException("':' is missing in accession number '"+accession+"'");
    }

    @Override
    protected String getTemplateURL(DbXref xref) {

        return CvDatabasePreferredLink.OBO.getLink();
    }

}