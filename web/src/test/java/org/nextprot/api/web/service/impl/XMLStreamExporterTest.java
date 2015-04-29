package org.nextprot.api.web.service.impl;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

@ActiveProfiles({"dev"})
public class XMLStreamExporterTest extends WebUnitBaseTest {

    @Test
    public void testExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        XMLStreamExporter exporter = new XMLStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview");
        //Mockito.verify(os, Mockito.times(4)).flush();
    }
}