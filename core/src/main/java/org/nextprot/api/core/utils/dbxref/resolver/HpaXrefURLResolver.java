package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class HpaXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("ENSG")) {
            if (accession.endsWith("subcellular")) {
                return CvDatabasePreferredLink.HPA_SUBCELL.getLink();
            }
            else {
                return CvDatabasePreferredLink.HPA_GENE.getLink();
            }
        }
        return CvDatabasePreferredLink.HPA_ANTIBODY.getLink();
    }
}