package org.nextprot.api.core.utils.dbxref;

class GenevisibleXrefURLResolver extends StampsS1S2XrefURLResolver {

    @Override
    protected String getS1StampValue(String primaryId) {
        return primaryId;
    }

    @Override
    protected String getS2StampValue(String primaryId) {
        return "HS";
    }
}