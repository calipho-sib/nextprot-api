package org.nextprot.api.web.service.impl.writer;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.ExportService;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by fnikitin on 12/08/15.
 */
public class NPEntryTXTWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testTXTExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Writer writer = new PrintWriter(out);

        NPEntryVelocityBasedWriter exporter = new NPEntryTXTWriter(writer);

        Map<String, Object> params = new HashMap<>();
        params.put(ExportService.ENTRIES_COUNT_PARAM, 2);

        exporter.write(Arrays.asList("NX_P06213", "NX_P01308"), "overview", params);

        assertEquals("#nb entries=2\nNX_P06213\nNX_P01308\n", out.toString());
    }
}