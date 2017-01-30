package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class GenevisibleXrefURLResolver extends DbXrefURLBaseResolver {
    
    @Override protected String getTemplateURL(DbXref xref) {
    	System.out.println("Hello pam");
        return "https://genevisible.com/tissues/HS/UniProt/%s";

    }


}