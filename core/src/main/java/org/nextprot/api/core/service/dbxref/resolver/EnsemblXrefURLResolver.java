package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;


class EnsemblXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

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
            return "https://www.ensembl.org/Multi/Search/Results?q=%s;site=ensembl";
        }
    }
}