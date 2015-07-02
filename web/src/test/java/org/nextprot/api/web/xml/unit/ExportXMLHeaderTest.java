package org.nextprot.api.web.xml.unit;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.core.domain.release.ReleaseContents;
import org.nextprot.api.web.dbunit.base.mvc.WebUnitBaseTest;
import org.nextprot.api.web.service.impl.NPStreamExporter;
import org.nextprot.api.web.service.impl.XMLStreamExporter;
import org.nextprot.api.web.utils.XMLUnitUtils;
import org.w3c.dom.NodeList;

public class ExportXMLHeaderTest extends WebUnitBaseTest {
	
    @Test
    public void testXMLExportHeaderRelease() throws Exception {

    	ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new PrintWriter(out);
        NPStreamExporter exporter = new XMLStreamExporter();
        
        Map<String, Object> map = new HashMap<String, Object>();        map.put("queryString", "something");        map.put("entriesCount", 2);
        ReleaseContents rc = new ReleaseContents();        rc.setApiRelease("api-test-version");        rc.setDatabaseRelease("database-test-version");        map.put("release", rc);
        
        exporter.export(new ArrayList<String>(), writer, "overview", map);

        NodeList dbReleaseNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/header/release/nextprot/database-release");
        assertEquals(dbReleaseNodes.item(0).getTextContent(), "database-test-version");
        NodeList apiReleaseNodes = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/header/release/nextprot/api-release");
        assertEquals(apiReleaseNodes.item(0).getTextContent(), "api-test-version");
        NodeList entriesCountNode = XMLUnitUtils.getMatchingNodes(out.toString(), "nextprot-export/header/entries-count");
        assertEquals(entriesCountNode.item(0).getTextContent(), "2");

    }

}
