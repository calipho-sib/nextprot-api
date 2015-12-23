package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;


class EmblXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String primaryId = xref.getAccession();

        if (xref.getAccession().indexOf('.') > 0) {
            primaryId = primaryId.split("\\.")[0];
        }

        return primaryId;
    }
}