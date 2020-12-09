package org.nextprot.api.core.export;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.export.EntryPartExporterImpl;
import org.nextprot.api.core.export.EntryPartWriterTSV;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@ActiveProfiles({ "dev","cache" })
public class EntryPartWriterTSVTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void getExpressionProfileOutputString() throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String subpart = "expression-profile";
        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").with(subpart));

        EntryPartWriterTSV writer = new EntryPartWriterTSV(EntryPartExporterImpl.fromSubPart(subpart), baos);
        writer.write(entry);

        String output = baos.toString(StandardCharsets.UTF_8.name());
        baos.close();

        String[] lines = output.split("\n");

        Assert.assertTrue(lines.length > 150);
        Assert.assertEquals("ENTRY_ACCESSION\tCATEGORY\tTERM_ACCESSION\tTERM_NAME\tQUALITY\tECO_ACCESSION\tECO_NAME\tNEGATIVE\tEXPRESSION_LEVEL\tSTAGE_ACCESSION\tSTAGE_NAME\tSOURCE\tURL", lines[0]);
    }
}