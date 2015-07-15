package org.nextprot.api.tasks.solr.indexer;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Terminology;
import org.nextprot.api.core.utils.TerminologyUtils;


public class CvTermSolrIndexer extends SolrIndexer<Terminology> {
	

	public CvTermSolrIndexer(String url) {
		super(url);
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Terminology terminology) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", terminology.getId());
		doc.addField("ac", terminology.getAccession());
		String filters=terminology.getOntology().replaceAll("[ _-]", "").toLowerCase().replaceAll("uniprot", "up").replaceAll("nextprot", "aanp");
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
		
		List<Terminology.TermProperty> properties = terminology.getProperties();
		if (properties != null) {
			doc.addField("properties",TerminologyUtils.convertPropertiesToString(properties));
		}
		
		List<DbXref> xrefs = terminology.getXrefs();
		// If filter is needed alternatively use: terminology.getFilteredXrefs(String category)
		if (xrefs != null) {
			doc.addField("other_xrefs",TerminologyUtils.convertXrefsToSolrString(xrefs));
		}
		
		return doc;
	}

}
