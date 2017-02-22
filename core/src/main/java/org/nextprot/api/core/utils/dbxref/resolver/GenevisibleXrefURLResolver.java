package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class GenevisibleXrefURLResolver extends DefaultDbXrefURLResolver {
    
    @Override
    public String getTemplateURL(DbXref xref) {
        return "https://genevisible.com/tissues/HS/UniProt/%s";
    }
}