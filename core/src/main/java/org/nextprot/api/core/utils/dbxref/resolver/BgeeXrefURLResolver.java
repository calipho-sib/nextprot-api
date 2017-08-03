package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class BgeeXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();
        String templateURL = super.getTemplateURL(xref);

        if (accession.contains("ENSG"))
            templateURL = "http://bgee.org/bgee/bgee?page=expression&action=data&%s";

        return templateURL;
    }

}