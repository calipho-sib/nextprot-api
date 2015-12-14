package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class UnigeneXrefURLResolver extends PlaceHoldersXrefURLResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

        return xref.getAccession().split("\\.")[1];
    }

    @Override
    protected String getFirstPlaceHolderValue(String primaryId) {
        return "Hs";
    }

    @Override
    protected String getSecondPlaceHolderValue(String primaryId) {
        return primaryId;
    }
}