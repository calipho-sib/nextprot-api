package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

public class EnsemblXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateUrl(DbXref xref) {

        String accession = xref.getAccession();
        String templateURL = xref.getLinkUrl();

        if (accession.startsWith("ENST")) {
            templateURL = CvDatabasePreferredLink.ENSEMBL_TRANSCRIPT.getLink();
        }
        else if (accession.startsWith("ENSP")) {
            templateURL = CvDatabasePreferredLink.ENSEMBL_PROTEIN.getLink();
        }
        else if (accession.startsWith("ENSG")) {
            templateURL = CvDatabasePreferredLink.ENSEMBL_GENE.getLink();
        }
        else {
            throw new UnresolvedXrefURLException("unknown accession number "+accession+": could not resolve template URL '" + templateURL);
        }

        return templateURL;
    }
}