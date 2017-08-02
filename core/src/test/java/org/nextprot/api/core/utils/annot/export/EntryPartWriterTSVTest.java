package org.nextprot.api.core.utils.annot.export;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })
public class EntryPartWriterTSVTest extends CoreUnitBaseTest {

    @Autowired
    private EntryBuilderService entryBuilderService;

    @Test
    public void getExpressionProfileOutputString() throws Exception {

        String subpart = "expression-profile";
        Entry entry = entryBuilderService.build(EntryConfig.newConfig("NX_P01308").with(subpart));

        EntryPartWriterTSV writer = new EntryPartWriterTSV(EntryPartExporterImpl.fromSubPart(subpart));
        writer.write(entry);

        String output = writer.getOutputString();
        String headers = output.split("\n")[0];
        Assert.assertEquals("ENTRY_ACCESSION\tTERM_ACCESSION\tTERM_NAME\tANNOTATION_QUALITY\tECO_ACCESSION\tECO_NAME\tEVIDENCE_ASSIGNED_BY\tEVIDENCE_QUALITY\tEXPRESSION_LEVEL\tSTAGE_ACCESSION\tSTAGE_NAME", headers);
    }
}