package org.nextprot.api.core.utils.dbxref.resolver;


import org.nextprot.api.core.domain.DbXref;


class WebInfoXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String resolve(DbXref xref) {

        return getAccessionNumber(xref);
    }

    @Override
    public String getTemplateURL(DbXref xref) {
        return "";
    }
}
