package org.nextprot.api.core.utils.dbxref;

import org.nextprot.api.core.domain.DbXref;

class BgeeXrefURLResolver extends DbXrefURLBaseResolver {

    @Override
    protected String getTemplateUrl(DbXref xref) {

        String accession = xref.getAccession();
        String templateURL = super.getTemplateUrl(xref);

        if (accession.contains("ENSG"))
            templateURL = templateURL.replace("uniprot_id=", "page=expression&action=data&");

        return templateURL;
    }
}