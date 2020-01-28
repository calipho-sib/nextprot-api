package org.nextprot.api.core.service.dbxref.resolver;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class HpaXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("ENSG")) {
            if (accession.endsWith("subcellular")) {
	           return CvDatabasePreferredLink.HPA_SUBCELL.getLink();
            }
            else {
	            return CvDatabasePreferredLink.HPA_GENE.getLink();
            }
        }
        else {
	        return CvDatabasePreferredLink.HPA_ANTIBODY.getLink();
        }
	}
}