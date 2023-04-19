package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class MassiveXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {
    	return CvDatabasePreferredLink.MASSIVE.getLink();
    }
    
    @Override
    public String resolve(DbXref xref) {
    	
    	String ac = xref.getAccession();

    	// if we're pointing a peptide
    	if (ac.startsWith("MSVp")) {  
        	String templateURL = getTemplateURL(xref);
        	return super.resolveURL(templateURL, ac);
    	
        // we're pointing a uniprot protein
    	} else {					
    		String templateURL = "https://massive.ucsd.edu/ProteoSAFe/protein_explorer.jsp?libraries=2&protein_name=%s";
        	return super.resolveURL(templateURL, ac);
    	}
    }
}
