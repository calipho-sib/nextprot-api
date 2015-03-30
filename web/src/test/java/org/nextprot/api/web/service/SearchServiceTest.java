package org.nextprot.api.web.service;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */

@ActiveProfiles({"pro"})
public class SearchServiceTest extends WebUnitBaseTest {

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
		request.setQuery("insulin");
		request.setQuality("quality=gold-and-silver");
		Set<String> accs = service.getAccessions(request);
		System.out.println(accs.size());
		
	}

}
