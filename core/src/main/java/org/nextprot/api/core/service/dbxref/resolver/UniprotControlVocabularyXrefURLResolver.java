package org.nextprot.api.core.service.dbxref.resolver;

import org.nextprot.api.core.domain.CvDatabasePreferredLink;
import org.nextprot.api.core.domain.DbXref;


class UniprotControlVocabularyXrefURLResolver extends DefaultDbXrefURLResolver {

//    @Override
//    public String getTemplateURL(DbXref xref) {

    @Override
    public String resolve(DbXref xref) {
    	
        String accession = xref.getAccession();
        String template;
        
        if (accession.startsWith("DI-")) {
            template = CvDatabasePreferredLink.UNIPROT_DISEASES.getLink();
        }
        else if (accession.startsWith("KW-")) {
            template = CvDatabasePreferredLink.UNIPROT_KEYWORDS.getLink();
        }
        else if (accession.startsWith("SL-")) {
            template = CvDatabasePreferredLink.UNIPROT_LOCATIONS.getLink();
        }       
        else {
        	// PTMs, families are also xref of this DB but have no external links
            template = null;
        }
        
        if (template==null) {
        	return "None";
        } else {
        	xref.setLinkUrl(template);
        	return super.resolve(xref);
        }
        
    }




}