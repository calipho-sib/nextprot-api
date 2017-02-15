package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class GenevisibleXrefURLResolver extends DbXrefURLBaseResolver {
    
    @Override protected String getTemplateURL(DbXref xref) {
        return "https://genevisible.com/tissues/HS/UniProt/%s";
    }


}