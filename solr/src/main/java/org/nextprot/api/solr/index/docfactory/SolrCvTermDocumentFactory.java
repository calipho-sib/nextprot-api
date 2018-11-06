package org.nextprot.api.solr.index.docfactory;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.TerminologyUtils;

import java.util.List;


public class SolrCvTermDocumentFactory extends SolrDocumentFactory<CvTerm> {

    public SolrCvTermDocumentFactory(CvTerm term) {

        super(term);
    }

    @Override
	public SolrInputDocument calcSolrInputDocument() {

        CvTerm term = solrizableObject;

		String ontology = term.getOntology();
		if (ontology.equals("OrganelleCv")) return null; // CaliphoMisc-194, ignore this ontology
		else if (ontology.equals("NextprotAnnotationCv")) return null; // CaliphoMisc-194, ignore this ontology
		else if (ontology.equals("UniprotFamilyCv")) return null; 

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", term.getId());
		doc.addField("ac", term.getAccession());
		String filters=term.getOntologyAltname().replaceAll("[ _-]", "").toLowerCase().replaceAll("uniprot", "up").replaceAll("nextprot", "aanp");
		doc.addField("filters", filters);
		doc.addField("name", term.getName());
		doc.addField("name_s", term.getName().toLowerCase());
		doc.addField("description", term.getDescription());
		
		List<String> synonstrings = term.getSynonyms();
		if (synonstrings != null) {
			int i = synonstrings.size();
			StringBuilder sb = new StringBuilder();
			for (String syn: synonstrings) {sb.append(syn); if (--i != 0) sb.append(" | "); }
			doc.addField("synonyms",sb.toString());
		}
		
		List<CvTerm.TermProperty> properties = term.getProperties();
		if (properties != null) {
			doc.addField("properties",TerminologyUtils.convertPropertiesToString(properties));
		}
		
		List<DbXref> xrefs = term.getXrefs();
		if (xrefs != null) {
			doc.addField("other_xrefs",TerminologyUtils.convertXrefsToSolrString(xrefs));
		}
		
		return doc;
	}
}
