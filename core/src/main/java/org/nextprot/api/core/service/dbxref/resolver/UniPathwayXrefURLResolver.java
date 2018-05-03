package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class UniPathwayXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {
    	xref.setLinkUrl(null);
        return null;
    }

    @Override
    public String resolve(DbXref xref) {
    	xref.setLinkUrl(null);
    	return "None";
    }

    
}