package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class NihArpXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String accession = xref.getAccession();

        // Db_URL: https://www.aidsreagent.org/reagentdetail.cfm?t=cell_lines&id=%s
        //   Note: %s is the value after the dash in the DR line.
        //   Example: for "DR   NIH-ARP; 11411-223": s%=223
        if (accession.contains("-")) return accession.replaceFirst("^.+-", "");

        throw new UnresolvedXrefURLException("'-' is missing in accession number '"+accession+"'");
    }
}