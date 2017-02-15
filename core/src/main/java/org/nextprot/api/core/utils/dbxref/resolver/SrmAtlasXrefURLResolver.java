package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class SrmAtlasXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String primaryId = xref.getPropertyByName("sequence").getValue();

        if (primaryId != null)
            return primaryId;

        throw new UnresolvedXrefURLException("missing primary id: could not find 'sequence' property");
    }
}