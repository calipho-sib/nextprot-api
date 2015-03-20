package org.nextprot.api.web.service;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */

@Ignore
public class SearchServiceTest extends WebUnitBaseTest {

	@Autowired
	private SearchService service;

	@Test
	public void shouldExportEntries() throws Exception {
		QueryRequest request = new QueryRequest();
		request.setQuery("insulin");
		Set<String> accs = service.getAssessions(request);
		assertTrue(accs.contains("NX_P01308"));
	}
}
