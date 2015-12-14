package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class ClinvarXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateUrl(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.matches("RCV\\d+")) {
            return CvDatabasePreferredLink.CLINVAR_MUTATION.getLink();
        }

        return CvDatabasePreferredLink.CLINVAR_GENE.getLink();
    }
}