package org.nextprot.api.web.xml.unit;

import org.junit.Test;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.impl.writer.EntryStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryVelocityBasedStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryXMLStreamWriter;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExportXMLHeaderTest extends WebUnitBaseTest {
	
    @Test
    public void testXMLExportHeaderRelease() throws Exception {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        EntryVelocityBasedStreamWriter exporter = new EntryXMLStreamWriter(writer, "overview", wac);
        
        ReleaseInfoVersions rc = new ReleaseInfoVersions();
        rc.setApiRelease("api-test-version");
        rc.setDatabaseRelease("database-test-version");

        Map<String, Object> infos = new HashMap<>();
        infos.put(EntryStreamWriter.getReleaseInfoKey(), rc);

        exporter.write(Collections.emptyList(), infos);

        NodeList dbReleaseNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "//*[local-name()='database-release']");
        assertEquals("database-test-version", dbReleaseNodes.item(0).getTextContent());
        NodeList apiReleaseNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "//*[local-name()='api-release']");
        assertEquals("api-test-version", apiReleaseNodes.item(0).getTextContent());
        NodeList entriesCountNode = XMLUnitUtils.getMatchingNodes(out.toString(), "//*[local-name()='number-of-entries']");
        assertEquals("0", entriesCountNode.item(0).getTextContent());
    }
}
