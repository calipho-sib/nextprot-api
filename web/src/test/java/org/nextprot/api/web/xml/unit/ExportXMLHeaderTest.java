package org.nextprot.api.web.xml.unit;

import org.junit.Test;
import org.nextprot.api.core.domain.release.ReleaseInfo;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.impl.writer.NPEntryVelocityBasedStreamWriter;
import org.nextprot.api.web.service.impl.writer.NPEntryXMLStreamWriter;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ExportXMLHeaderTest extends WebUnitBaseTest {
	
    @Test
    public void testXMLExportHeaderRelease() throws Exception {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        NPEntryVelocityBasedStreamWriter exporter = new NPEntryXMLStreamWriter(writer, "overview");
        
        Map<String, Object> map = new HashMap<>();

        map.put("queryString", "something");
        map.put(ExportService.ENTRIES_COUNT_PARAM, 2);

        ReleaseInfo rc = new ReleaseInfo();
        rc.setApiRelease("api-test-version");
        rc.setDatabaseRelease("database-test-version");

        map.put("release", rc);
        
        exporter.write(new ArrayList<String>(), map);

        NodeList dbReleaseNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "//*[local-name()='database-release']");
        assertEquals(dbReleaseNodes.item(0).getTextContent(), "database-test-version");
        NodeList apiReleaseNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "//*[local-name()='api-release']");
        assertEquals(apiReleaseNodes.item(0).getTextContent(), "api-test-version");
        NodeList entriesCountNode = XMLUnitUtils.getMatchingNodes(out.toString(), "//*[local-name()='number-of-entries']");
        assertEquals(entriesCountNode.item(0).getTextContent(), "2");
    }
}
