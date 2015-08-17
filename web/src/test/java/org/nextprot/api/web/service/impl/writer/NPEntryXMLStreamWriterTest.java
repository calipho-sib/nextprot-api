package org.nextprot.api.web.service.impl.writer;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by fnikitin on 12/08/15.
 */
public class NPEntryXMLStreamWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testXMLExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        NPEntryVelocityBasedStreamWriter exporter = new NPEntryXMLStreamWriter(writer, "overview");
        exporter.write(Arrays.asList("NX_P06213", "NX_P01308"));

        NodeList recommendedNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/entry-list/entry/overview/gene-list/gene/gene-name[@type='primary']");
        assertEquals(recommendedNodes.item(0).getTextContent(), "INSR");
        assertEquals(recommendedNodes.item(1).getTextContent(), "INS");
    }
}