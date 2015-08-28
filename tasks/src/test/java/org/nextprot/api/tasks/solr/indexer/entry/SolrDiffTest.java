package org.nextprot.api.tasks.solr.indexer.entry;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"dev"})
public class SolrDiffTest extends CoreUnitBaseTest {

	HttpSolrServer solr = new HttpSolrServer("http://kant.isb-sib.ch:8983/solr/npentries1");

	protected Object getValueForFieldInCurrentSolrImplementation(String entryName, Fields field) {

		List<Object> result = new ArrayList<Object>();

		try {
			SolrQuery query = new SolrQuery();
			query.setQuery("id:" + entryName);
			query.setFields(field.getName());
			QueryResponse response;
			response = solr.query(query);
			SolrDocumentList solrResults = response.getResults();

			for (int i = 0; i < solrResults.size(); ++i) {
				result.add(solrResults.get(i).get(field.getName()));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return result.get(0);
	}

}
