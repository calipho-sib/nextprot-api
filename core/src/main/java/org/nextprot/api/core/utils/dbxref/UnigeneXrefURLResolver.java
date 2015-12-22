package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class UnigeneXrefURLResolver extends StampsS1S2XrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        return xref.getAccession().split("\\.")[1];
    }

    @Override
    protected String getS1StampValue(String primaryId) {
        return "Hs";
    }

    @Override
    protected String getS2StampValue(String primaryId) {
        return primaryId;
    }
}