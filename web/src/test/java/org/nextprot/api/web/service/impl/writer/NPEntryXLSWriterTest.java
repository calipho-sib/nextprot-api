package org.nextprot.api.web.service.impl.writer;

import org.junit.Test;
import org.nextprot.api.web.dbunit.base.mvc.WebIntegrationBaseTest;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Created by fnikitin on 12/08/15.
 */
public class NPEntryXLSWriterTest extends WebIntegrationBaseTest {

    @Test
    public void testXLSExportStream() throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        NPEntryOutputStreamWriter exporter = new NPEntryXLSWriter(out);

        exporter.write(Arrays.asList("NX_P48730"), "overview", null);
    }
}