package org.nextprot.api.web.service;

import org.junit.Test;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

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
		Set<String> accs = service.getAccessions(request);
		assertTrue(accs.contains("NX_P01308"));
	}

	@Test
	public void shouldNotContainThatManyEntries() throws Exception {
		QueryRequest request = new QueryRequest();
		request.setQuery("daniel teixeiracarvalho ");
		request.setQuality("quality=gold-and-silver");
		Set<String> accs = service.getAccessions(request);
		assertTrue(accs.size() < 10);
	}

}
