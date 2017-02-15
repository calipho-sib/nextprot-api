package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class PeptideAtlasXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (!accession.startsWith("PAp")) {
            return CvDatabasePreferredLink.PEPTIDE_ATLAS_PROTEIN.getLink();
        }
        else {
            return CvDatabasePreferredLink.PEPTIDE_ATLAS_PEPTIDE.getLink();
        }
    }
}