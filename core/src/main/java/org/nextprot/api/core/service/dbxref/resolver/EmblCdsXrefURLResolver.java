package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class EmblCdsXrefURLResolver extends DefaultDbXrefURLResolver {
    
    @Override
    public String getTemplateURL(DbXref xref) {
        return "https://www.ebi.ac.uk/ena/data/view/%s";
    }
}