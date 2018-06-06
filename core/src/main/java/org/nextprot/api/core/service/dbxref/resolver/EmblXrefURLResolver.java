package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;


class EmblXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String primaryId = xref.getAccession();

        if (xref.getAccession().contains(".")) {
            primaryId = primaryId.split("\\.")[0];
        }

        return primaryId;
    }
}