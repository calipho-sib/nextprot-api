package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class LowerCaseAccessionXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession().toLowerCase();
    }
}