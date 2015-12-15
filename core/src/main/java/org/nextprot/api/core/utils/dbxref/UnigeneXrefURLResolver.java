package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class UnigeneXrefURLResolver extends S1S2PlaceHoldersXrefURLResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

        return xref.getAccession().split("\\.")[1];
    }

    @Override
    protected String getS1PlaceHolderValue(String primaryId) {
        return "Hs";
    }

    @Override
    protected String getS2PlaceHolderValue(String primaryId) {
        return primaryId;
    }
}