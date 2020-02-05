package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;

class VariOXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

	    return CvDatabasePreferredLink.VARIO.getLink();
    }

    @Override
    public String getAccessionNumber(DbXref xref) {
    	
    	return xref.getAccession().replace(":","_");
    }
    
}