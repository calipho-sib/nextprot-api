package org.nextprot.api.core.utils.dbxref.resolver;


import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;


class WebInfoXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    public String resolve(DbXref xref) {

        return getAccessionNumber(xref);
    }

    @Override
    protected String getTemplateURL(DbXref xref) {
        return "";
    }
}
