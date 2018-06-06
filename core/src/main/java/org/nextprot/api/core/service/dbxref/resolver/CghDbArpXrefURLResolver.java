package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class CghDbArpXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    protected String getAccessionNumber(DbXref xref) {

        String accession = xref.getAccession();

        // Db_URL: http://www.cghtmd.jp/CGHDatabase/mapViewer?hid=%s&aid=%t&lang=en
        //   Note: %s and %t are respectively the values before and after the dash in the DR line.
        //   Example: for "DR   CGH-DB; 9029-4": s%=9029, t%=4
        if (accession.contains("-"))
            return accession.replaceFirst("-.+$", "");

        throw new UnresolvedXrefURLException("'-' is missing in accession number '"+accession+"'");
    }

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.contains("-")) {
            accession = accession.replaceFirst("^.+-", "");

            String templateURL = super.getTemplateURL(xref);

            if (templateURL.contains("%t"))
                return templateURL.replaceFirst("%t", accession.replaceFirst("^.+-", ""));

            throw new UnresolvedXrefURLException("placeholder '%t' is missing: could not resolve template URL '"+templateURL+"'");
        }
        throw new UnresolvedXrefURLException("'-' is missing in accession number '"+accession+"'");
    }
}