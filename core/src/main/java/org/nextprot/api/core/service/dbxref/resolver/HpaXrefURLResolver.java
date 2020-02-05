package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class HpaXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = getAccessionNumber(xref);
        
        if (accession.startsWith("ENSG")) {
            if (accession.endsWith("cell")) {
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
    
    @Override
    public String getAccessionNumber(DbXref xref) {
    	String ac = xref.getAccession();
    	// convert old URL path to new one
    	if (ac.endsWith("subcellular")) {
    		return ac.substring(0, ac.length()-11) + "cell";
    	} else {
            return xref.getAccession();    		
    	}
    }

}