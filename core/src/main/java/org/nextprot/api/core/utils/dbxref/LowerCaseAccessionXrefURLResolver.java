package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class LowerCaseAccessionXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

        return xref.getAccession().toLowerCase();
    }
}