package org.nextprot.api.core.utils.dbxref;


import org.nextprot.api.core.domain.DbXref;

public class WebInfoXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    public String resolve(DbXref xref) {

        return getPrimaryId(xref);
    }

    @Override
    protected String getTemplateUrl(DbXref xref) {
        return "";
    }
}
