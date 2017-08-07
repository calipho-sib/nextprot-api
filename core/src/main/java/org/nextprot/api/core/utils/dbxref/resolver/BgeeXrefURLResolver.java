package org.nextprot.api.core.utils.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class BgeeXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();
        String templateURL = super.getTemplateURL(xref);

        if (accession.startsWith("ENSG") && ! accession.contains("&")) {  // ENSG id only
            templateURL = "http://bgee.org/bgee/bgee?page=expression&action=data&gene_id=%s";        	
        } else if (accession.contains("ENSG")) {  // several parameters: stage, ...
            templateURL = "http://bgee.org/bgee/bgee?page=expression&action=data&%s";
        } else {
        	templateURL = "http://bgee.org/bgee/bgee?uniprot_id=%s";
        }
        return templateURL;
    }

}