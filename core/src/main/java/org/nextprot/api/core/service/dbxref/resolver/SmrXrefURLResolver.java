package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class SmrXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        return "https://swissmodel.expasy.org/repository/uniprot/%s";

    }
}