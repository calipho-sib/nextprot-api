package org.nextprot.api.web.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */

public class SearchServiceTest extends WebIntegrationBaseTest {

	@Autowired
	private SearchService service;

	@Test
	public void shouldExportEntries() throws Exception {
		QueryRequest request = new QueryRequest();
		request.setQuality("gold");
		request.setQuery("insulin");
		Set<String> accs = service.findAccessions(request);
		assertTrue(accs.contains("NX_P01308"));
	}

	@Test
	public void shouldNotContainThatManyEntries() throws Exception {
		QueryRequest request = new QueryRequest();
		request.setQuery("daniel teixeiracarvalho ");
		request.setQuality("quality=gold-and-silver");
		Set<String> accs = service.findAccessions(request);
		assertTrue(accs.size() < 10);
	}

	@Test
	public void shouldSortBigNumberOfEntries() {

		String originalQuery = "DNA";

		QueryRequest request = new QueryRequest();
		request.setQuery(originalQuery);
		request.setQuality("quality=gold-and-silver");
		request.setSort(SortConfig.Criteria.AC.getName());

		Set<String> accs = service.findAccessions(request);
		List<String> sorted = service.sortAccessionsWithSolr(request, accs);

		Assert.assertEquals(originalQuery, request.getQuery());
		Assert.assertEquals(sorted.size(), accs.size());
	}

}
