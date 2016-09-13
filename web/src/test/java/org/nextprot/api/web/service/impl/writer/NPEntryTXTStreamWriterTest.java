package org.nextprot.api.web.service.impl.writer;

import org.junit.Test;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;
import org.nextprot.api.web.service.ExportService;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class NPEntryTXTStreamWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testTXTExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Writer writer = new PrintWriter(out);

        EntryVelocityBasedStreamWriter exporter = new EntryTXTStreamWriter(writer);

        Map<String, Object> params = new HashMap<>();
        params.put(ExportService.ENTRIES_COUNT_PARAM, 2);

        exporter.write(Arrays.asList("NX_P06213", "NX_P01308"), params);

        assertEquals("#nb entries=2"+ StringUtils.CR_LF +"NX_P06213"+ StringUtils.CR_LF+"NX_P01308"+ StringUtils.CR_LF, out.toString());
    }
}