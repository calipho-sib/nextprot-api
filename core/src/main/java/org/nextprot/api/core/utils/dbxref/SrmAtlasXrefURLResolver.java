package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class SrmAtlasXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

        String primaryId = xref.getPropertyByName("sequence").getValue();

        if (primaryId != null)
            return primaryId;

        throw new UnresolvedXrefURLException("missing primary id: could not find 'sequence' property");
    }
}