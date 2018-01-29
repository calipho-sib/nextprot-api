package org.nextprot.api.core.service.export.io;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

// TODO: test should not be ignored once db changed
@Ignore
@ActiveProfiles({ "dev,cache" })
public class EntryProteinExistenceReportTSVWriterTest extends CoreUnitBaseTest {

    @Autowired
    private OverviewService overviewService;

    @Test
    public void testExport() {

        StringOutputStream sos = new StringOutputStream();
        EntryProteinExistenceReportTSVWriter writer = new EntryProteinExistenceReportTSVWriter(sos);

        writer.write("NX_Q6SJ96", overviewService.findOverviewByEntry("NX_Q6SJ96").getProteinExistences());
        writer.close();

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertEquals(2, observedLines.length);
        Assert.assertEquals("NX_Q6SJ96\tEvidence at protein level\tEvidence at transcript level\tEvidence at protein level\tPromote to PE1 based on expression data", observedLines[1]);
    }
}