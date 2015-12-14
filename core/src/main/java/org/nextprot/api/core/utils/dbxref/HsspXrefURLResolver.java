package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class HsspXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getPrimaryId(DbXref xref) {

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