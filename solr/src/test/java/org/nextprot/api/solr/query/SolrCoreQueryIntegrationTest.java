package org.nextprot.api.solr.query;

import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.SearchConnectionException;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.SolrGoldAndSilverEntryCore;
import org.nextprot.api.solr.core.impl.SolrGoldOnlyEntryCore;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.dto.SearchResult;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SolrCoreQueryIntegrationTest {

    @Test
	public void testRBRFamilyProteins() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> coreGoldOnly = buildEntrySolrCore("crick", EnumSet.of(EntrySolrField.ID), true);
		SolrCore<EntrySolrField> coreGoldAndSilver = buildEntrySolrCore("crick", EnumSet.of(EntrySolrField.ID), false);

		for (SolrCore<EntrySolrField> core : Arrays.asList(coreGoldAndSilver, coreGoldOnly)) {

			Map<String, Set<String>> expectedProteins = new HashMap<>();
			expectedProteins.put("FA-03242", Sets.newHashSet("NX_O95376", "NX_Q9Y4X5"));
			expectedProteins.put("FA-03243", Sets.newHashSet("NX_O60260"));
			expectedProteins.put("FA-03244", Sets.newHashSet("NX_Q9UBS8"));
			expectedProteins.put("FA-03245", Sets.newHashSet("NX_Q7Z419", "NX_P50876"));
			expectedProteins.put("FA-03246", Sets.newHashSet("NX_Q6ZMZ0", "NX_Q9NV58"));
			expectedProteins.put("FA-03247", Sets.newHashSet("NX_Q8TC41"));
			expectedProteins.put("FA-03241", Sets.newHashSet("NX_Q9P2G1", "NX_O95376", "NX_Q9Y4X5", "NX_O60260",
					"NX_Q9UBS8", "NX_Q7Z419", "NX_P50876", "NX_Q6ZMZ0", "NX_Q9NV58", "NX_Q8TC41"));

			for (String familyAc : expectedProteins.keySet()) {

				Query<EntrySolrField> query = new Query<>(core).rows(50).addQuery(familyAc);

				SearchResult response = executeQuery(query);
				Assert.assertEquals(expectedProteins.get(familyAc).size(), response.getFound());

				Set<String> acs = response.getResults().stream()
						.map(r -> (String) r.get(EntrySolrField.ID.getName()))
						.collect(Collectors.toSet());
				Assert.assertEquals(expectedProteins.get(familyAc), acs);
			}
		}
	}

	@Test
	public void testSortingInPLMode() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("crick", EnumSet.of(EntrySolrField.ID, EntrySolrField.RECOMMENDED_GENE_NAMES), true);

		Query<EntrySolrField> query = new Query<>(core).rows(50).addQuery("MSH6").sort(SortConfig.Criteria.GENE);
		query.setQueryMode(QueryMode.PROTEIN_LIST_SEARCH);

		SearchResult response = executeQuery(query);

		Assert.assertEquals(72, response.getFound());
		List<Map<String, Object>> results = response.getResults();

		String geneName = (String) results.get(0).get("recommended_gene_names");

		for (int i=1 ; i<50 ; i++) {

			Assert.assertTrue(geneName.compareTo((String)results.get(i).get("recommended_gene_names")) < 0);
			geneName = (String)results.get(i).get("recommended_gene_names");
		}
	}

	public static SolrCore<EntrySolrField> buildEntrySolrCore(String hostname, Set<EntrySolrField> fl, boolean isGold) {

		return (isGold) ? new SolrGoldOnlyEntryCore("http://"+hostname+":8983/solr", fl) :
				new SolrGoldAndSilverEntryCore("http://"+hostname+":8983/solr", fl);
	}

	public static SearchResult executeQuery(Query query) throws QueryConfiguration.MissingSortConfigException {

		SolrCore core = query.getSolrCore();

		QueryExecutor executor = new QueryExecutor(core);

		try {
			return executor.execute(query.getQueryConfiguration().convertQuery(query));
		} catch (SolrServerException e) {
			throw new SearchConnectionException("Could not connect to Solr server. Please contact support or try again later.");
		}
	}
}