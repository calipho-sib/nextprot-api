package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class BgeeXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();
        String templateURL = super.getTemplateURL(xref);

        if (accession.contains("ENSG"))
            //templateURL = templateURL.replaceFirst("uniprot_id=", "page=expression&action=data&");
            templateURL = "http://bgee.org/?page=gene&gene_id=%s";

        return templateURL;
    }
}