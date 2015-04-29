package org.nextprot.api.web.service;

import org.junit.Ignore;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */

@Ignore
@ActiveProfiles({"pro"})
public class ExportServiceTest extends WebUnitBaseTest {

	/*@Autowired
	private ExportService service;

	@Test
	public void shouldExportEntries() throws Exception {
		OutputStream os = Mockito.mock(OutputStream.class);
		service.streamResultsInXML(new PrintWriter(System.out), "overview",  Arrays.asList("NX_P06213", "NX_P01308"), false, false);
		Mockito.verify(os, Mockito.times(4)).flush();
	}

	@Test
	public void shouldExportEntriesInOutputStream() throws Exception {
		OutputStream os = new FileOutputStream(new File("tmp.xml"));
		service.streamResultsInXML(new PrintWriter(System.out), "overview",  Arrays.asList("NX_P06213", "NX_P01308"), false, false);
		os.close();
	}
	
	@Test
	public void shouldExportEntriesInJson() throws Exception {
		service.streamResultsInJson(new PrintWriter(System.out), "overview",  Arrays.asList("NX_P06213", "NX_P01308"));
	}*/


}
