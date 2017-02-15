package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class SMRXrefURLResolver extends DbXrefURLBaseResolver {


    @Override
    protected String getTemplateURL(DbXref xref) {

        return "https://swissmodel.expasy.org/repository/uniprot/%s";

    }
	

}