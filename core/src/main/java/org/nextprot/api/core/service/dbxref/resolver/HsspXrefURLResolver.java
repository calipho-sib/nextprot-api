package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class HsspXrefURLResolver extends DefaultDbXrefURLResolver {

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