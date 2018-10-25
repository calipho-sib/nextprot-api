package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.CvTerm;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.utils.TerminologyUtils;
import org.nextprot.api.tasks.solr.SimpleHttpSolrServer;
import org.nextprot.api.tasks.solr.SimpleSolrServer;

import java.util.List;


public class CvTermSolrIndexer extends SolrIndexer<CvTerm> {
	
	public CvTermSolrIndexer(String url) {
		this(new SimpleHttpSolrServer(url));
	}

    public CvTermSolrIndexer(SimpleSolrServer solrServer) {
        super(solrServer);
    }

	@Override
	public SolrInputDocument convertToSolrDocument(CvTerm terminology) {
		
		String ontology = terminology.getOntology();
		if (ontology.equals("OrganelleCv")) return null; // CaliphoMisc-194, ignore this ontology
		else if (ontology.equals("NextprotAnnotationCv")) return null; // CaliphoMisc-194, ignore this ontology
		else if (ontology.equals("UniprotFamilyCv")) return null; 

		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", terminology.getId());
		doc.addField("ac", terminology.getAccession());
		String filters=terminology.getOntologyAltname().replaceAll("[ _-]", "").toLowerCase().replaceAll("uniprot", "up").replaceAll("nextprot", "aanp");
		doc.addField("filters", filters);
		doc.addField("name", terminology.getName());
		doc.addField("name_s", terminology.getName().toLowerCase());
		doc.addField("description", terminology.getDescription());
		
		List<String> synonstrings = terminology.getSynonyms();
		if (synonstrings != null) {
			int i = synonstrings.size();
			StringBuilder sb = new StringBuilder();
			for (String syn: synonstrings) {sb.append(syn); if (--i != 0) sb.append(" | "); }
			doc.addField("synonyms",sb.toString());
		}
		
		List<CvTerm.TermProperty> properties = terminology.getProperties();
		if (properties != null) {
			doc.addField("properties",TerminologyUtils.convertPropertiesToString(properties));
		}
		
		List<DbXref> xrefs = terminology.getXrefs();
		if (xrefs != null) {
			doc.addField("other_xrefs",TerminologyUtils.convertXrefsToSolrString(xrefs));
		}
		
		return doc;
	}
}
