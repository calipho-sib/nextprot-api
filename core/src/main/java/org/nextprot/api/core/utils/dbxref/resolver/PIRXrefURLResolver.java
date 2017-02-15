package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;


class PirXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        DbXref.DbXrefProperty property = xref.getPropertyByName("entry name");

        if (property != null)
            return property.getValue();

        throw new UnresolvedXrefURLException("missing accession number: could not find 'entry name' property");
    }
}