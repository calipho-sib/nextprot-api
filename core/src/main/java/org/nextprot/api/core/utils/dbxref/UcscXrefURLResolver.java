package org.nextprot.api.core.utils.dbxref;

class UcscXrefURLResolver extends S1S2PlaceHoldersXrefURLResolver {

    @Override
    protected String getS1PlaceHolderValue(String primaryId) {
        return primaryId;
    }

    @Override
    protected String getS2PlaceHolderValue(String primaryId) {
        return "human";
    }
}