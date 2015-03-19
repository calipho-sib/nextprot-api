package org.nextprot.api.web.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */

@ActiveProfiles({"pro"})
public class ExportServiceTest extends WebUnitBaseTest {

	@Autowired
	private ExportService service;

	@Test
	public void shouldExportEntries() throws Exception {
		OutputStream os = Mockito.mock(OutputStream.class);
		service.streamResultsInXML(os, "overview",  new HashSet<String>(Arrays.asList("NX_P06213", "NX_P01308")));
		System.out.println("done");
		Mockito.verify(os, Mockito.times(4)).flush();
	}

	@Test
	public void shouldExportEntriesInOutputStream() throws Exception {
		OutputStream os = new FileOutputStream(new File("tmp.xml"));
		service.streamResultsInXML(os, "overview",  new HashSet<String>(Arrays.asList("NX_P06213", "NX_P01308")));
		os.close();
	}
	
	@Test
	public void shouldExportEntriesInJson() throws Exception {
		service.streamResultsInJson(System.out, "overview",  new HashSet<String>(Arrays.asList("NX_P06213", "NX_P01308")));
	}


}
