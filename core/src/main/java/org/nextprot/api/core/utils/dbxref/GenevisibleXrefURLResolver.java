package org.nextprot.api.core.utils.dbxref;

class GenevisibleXrefURLResolver extends PlaceHoldersXrefURLResolver {

    @Override
    protected String getFirstPlaceHolderValue(String primaryId) {
        return primaryId;
    }

    @Override
    protected String getSecondPlaceHolderValue(String primaryId) {
        return "HS";
    }
}