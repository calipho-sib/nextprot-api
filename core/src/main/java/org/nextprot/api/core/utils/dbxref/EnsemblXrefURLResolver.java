package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

public class EnsemblXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateUrl(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("ENST")) {
            return CvDatabasePreferredLink.ENSEMBL_TRANSCRIPT.getLink();
        }
        else if (accession.startsWith("ENSP")) {
            return CvDatabasePreferredLink.ENSEMBL_PROTEIN.getLink();
        }
        else if (accession.startsWith("ENSG")) {
            return CvDatabasePreferredLink.ENSEMBL_GENE.getLink();
        }
        else {
            throw new UnresolvedXrefURLException("unknown accession number '"+accession+"': valid identifier should have prefix ENST, ENSP or ENSG");
        }
    }
}