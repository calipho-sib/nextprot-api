package org.nextprot.api.web.service.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.core.domain.release.ReleaseContents;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.w3c.dom.NodeList;

public class StreamExporterTest extends WebIntegrationBaseTest {

    @Autowired
    private FluentEntryService fluentEntryService;

    @Autowired
    private VelocityConfig velocityConfig;
    

    @Test
    public void testXMLExportStream() throws Exception {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);

        NPStreamExporter exporter = new XMLStreamExporter();

        Map<String, Object> map = new HashMap<String, Object>();        map.put("queryString", "something");        map.put("entriesCount", 2);
        ReleaseContents rc = new ReleaseContents();        rc.setApiRelease("yo");        rc.setDatabaseRelease("2020");        map.put("release", rc);
        
        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview", map);
        
        System.err.println(out.toString());
        
        NodeList nodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/entry-list/entry/overview");
        assertEquals(2, nodes.getLength());

        NodeList recommendedNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/entry-list/entry/overview/gene-list/gene/gene-name[@type='primary']");
        assertEquals(recommendedNodes.item(0).getTextContent(), "INSR");
        assertEquals(recommendedNodes.item(1).getTextContent(), "INS");
        
    }

    @Test
    public void testJSONExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new JSONStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview", null);
    }

    @Test
    public void testFastaExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new FastaStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview", null);
    }

    @Test
    public void testPeffExportStream() throws Exception {

        Writer writer = new PrintWriter(System.out);

        NPStreamExporter exporter = new PeffStreamExporter();

        exporter.export(Arrays.asList("NX_P06213", "NX_P01308"), writer, "overview", null);
    }
}