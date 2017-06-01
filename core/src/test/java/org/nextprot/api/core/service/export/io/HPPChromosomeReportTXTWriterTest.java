package org.nextprot.api.core.service.export.io;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.export.HPPChromosomeReportWriter;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collections;

import static org.nextprot.api.core.domain.EntryReportTest.newEntryReport;

@ActiveProfiles({ "dev" })
public class HPPChromosomeReportTXTWriterTest extends CoreUnitBaseTest {

    @Autowired
    private OverviewService overviewService;

    @Test
    public void writeChromosomeReport() throws Exception {

        ChromosomeReport report = new ChromosomeReport();
        report.setDataRelease("2017-01-23");

        ChromosomeReport.Summary summary = new ChromosomeReport.Summary();
        summary.setChromosome("Y");
        summary.setGeneCount(58);
        summary.setEntryCount(48);

        report.setSummary(summary);

        EntryReport entryReport1 = newEntryReport("SRY", "NX_Q05066", "Yp11.2",
                "2786855", "2787699", ProteinExistenceLevel.UNCERTAIN,
                false, true, true, true, 1, 47, 1,
                "Sex-determining region Y protein");

        EntryReport entryReport2 = newEntryReport("RBMY1C", "NX_P0DJD4", "Yq11.23",
                "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, true, false, false, 1, 0, 0,
                "RNA-binding motif protein, Y chromosome, family 1 member C");

        EntryReport entryReport3 = newEntryReport("MFSD14A", "NX_Q96MC6", "1p21.2",
                "100038097", "100083377", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, false, false, false, 1, 141, 3,
                "Hippocampus abundant transcript 1 protein");

        EntryReport entryReport4 = newEntryReport("VCY", "NX_O14598", "Yq11.221",
                "13985772", "13986513", ProteinExistenceLevel.PROTEIN_LEVEL,
                true, true, false, false, 1, 4, 0,
                "Testis-specific basic protein Y 1");

        EntryReport entryReport5 = newEntryReport("VCY", "NX_O14598", "Yq11.221",
                "14056217", "14056958", ProteinExistenceLevel.PROTEIN_LEVEL,
                true, true, false, false, 1, 4, 0,
                "Testis-specific basic protein Y 1");

        report.setEntryReports(Arrays.asList(entryReport1, entryReport2, entryReport3, entryReport4, entryReport5));

        StringOutputStream sos = new StringOutputStream();

        HPPChromosomeReportWriter writer = new HPPChromosomeReportTXTWriter(sos, overviewService);
        writer.write(report);

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertEquals(5, observedLines.length);
        Assert.assertEquals("neXtProt AC  Gene name(s) Protein existence         Proteomics Antibody", observedLines[0]);
        Assert.assertEquals("NX_O14598    VCY;VCY1B    Evidence at protein level yes        yes     ", observedLines[1]);
        Assert.assertEquals("NX_P0DJD4    RBMY1C       Evidence at protein level no         yes     ", observedLines[2]);
        Assert.assertEquals("NX_Q05066    SRY          Uncertain                 no         yes     ", observedLines[3]);
        Assert.assertEquals("NX_Q96MC6    MFSD14A      Evidence at protein level no         no      ", observedLines[4]);
    }

    @Test
    public void writeChromosomeReportUnknownGeneNames() throws Exception {

        ChromosomeReport report = new ChromosomeReport();
        report.setDataRelease("2017-01-23");

        ChromosomeReport.Summary summary = new ChromosomeReport.Summary();
        summary.setChromosome("unknown");
        summary.setGeneCount(5);
        summary.setEntryCount(5);

        report.setSummary(summary);

        EntryReport entryReport1 = newEntryReport(null, "NX_O00370", null,
                "-", "-", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, false, true, false, 1, 0, 0,
                "LINE-1 retrotransposable element ORF2 protein");

        report.setEntryReports(Collections.singletonList(entryReport1));

        StringOutputStream sos = new StringOutputStream();

        HPPChromosomeReportWriter writer = new HPPChromosomeReportTXTWriter(sos, overviewService);
        writer.write(report);

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertEquals(2, observedLines.length);
        Assert.assertEquals("neXtProt AC  Gene name(s) Protein existence         Proteomics Antibody", observedLines[0]);
        Assert.assertEquals("NX_O00370    -            Evidence at protein level no         no      ", observedLines[1]);
    }
}