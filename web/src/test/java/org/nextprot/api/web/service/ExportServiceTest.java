package org.nextprot.api.web.service;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.CommonsUnitBaseTest;
import org.nextprot.api.core.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */
public class ExportServiceTest extends CommonsUnitBaseTest {

	@Autowired
	private ExportService service;


	@Test
	public void shouldExportEntries() throws Exception {
		service.streamResultsInXML(new PrintWriter(System.out), "protein-sequence",  new HashSet<String>(Arrays.asList("NX_P06213", "NX_P01308")));
	}

}
