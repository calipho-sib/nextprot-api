package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.dbxref.DbXrefURLBaseResolver;

class HsspXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        DbXref.DbXrefProperty pdbAccession = xref.getPropertyByName("PDB accession");

        String primaryId;

        if (pdbAccession != null && pdbAccession.getValue() != null) {
            primaryId = xref.getPropertyByName("PDB accession").getValue().toLowerCase();
        } else {
            primaryId = xref.getAccession().toLowerCase();
        }

        return primaryId;
    }
}