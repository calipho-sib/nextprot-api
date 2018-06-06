package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.DbXref;

class BgeeXrefURLResolver extends DefaultDbXrefURLResolver {

    @Override
    public String getTemplateURL(DbXref xref) {

        String accession = xref.getAccession();

        if (accession.startsWith("ENSG") && ! accession.contains("&")) {  // ENSG id only
            return "http://bgee.org/bgee/bgee?page=expression&action=data&gene_id=%s";
        } else if (accession.contains("ENSG")) {  // several parameters: stage, ...
            return "http://bgee.org/bgee/bgee?page=expression&action=data&%s";
        } else {
        	return "http://bgee.org/bgee/bgee?uniprot_id=%s";
        }
    }
}