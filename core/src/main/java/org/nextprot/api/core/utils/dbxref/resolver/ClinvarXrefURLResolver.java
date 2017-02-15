package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class ClinvarXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.matches("RCV\\d+")) {
            return CvDatabasePreferredLink.CLINVAR_MUTATION.getLink();
        }

        return CvDatabasePreferredLink.CLINVAR_GENE.getLink();
    }
}