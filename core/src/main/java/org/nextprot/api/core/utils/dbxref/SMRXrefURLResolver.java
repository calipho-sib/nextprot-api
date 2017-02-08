package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

import com.google.common.base.Preconditions;

class SMRXrefURLResolver extends DbXrefURLBaseResolver {


    @Override
    protected String getTemplateURL(DbXref xref) {

        return "https://swissmodel.expasy.org/repository/uniprot/%s";

    }
	

}