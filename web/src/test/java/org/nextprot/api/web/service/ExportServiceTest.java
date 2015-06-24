package org.nextprot.api.web.service;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

/**
 * Exports an entry
 * 
 * @author dteixeira
 */
@Ignore
public class ExportServiceTest extends WebIntegrationBaseTest {

	@Autowired
	private FluentEntryService fluentEntryService;

	@Autowired
	private VelocityConfig velocityConfig;

	@Autowired
	private ExportService service;

	@Test
	public void shouldExportEntriesInXML() throws Exception {

		/*OutputStream os = Mockito.mock(OutputStream.class);
		service.streamResultsInXML(new PrintWriter(System.out), "overview", Arrays.asList("NX_P06213", "NX_P01308"), false, false);
		Mockito.verify(os, Mockito.times(4)).flush();*/

		Writer writer = new PrintWriter(System.out);
		service.streamResults(NPFileFormat.XML, writer, "overview", Arrays.asList("NX_P06213", "NX_P01308"));
	}

	@Test
	public void shouldExportEntriesInJson() throws Exception {

		/*Writer writer = new PrintWriter(System.out);
		NPStreamExporter exporter = NPFileExporter.XML.getNPStreamExporter();
		exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview");*/

		Writer writer = new PrintWriter(System.out);
		service.streamResults(NPFileFormat.JSON, writer, "overview", Arrays.asList("NX_P06213", "NX_P01308"));
	}
}
