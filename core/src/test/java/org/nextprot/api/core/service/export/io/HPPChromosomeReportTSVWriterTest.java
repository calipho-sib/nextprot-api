package org.nextprot.api.core.service.export.io;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.OverviewService;
import org.nextprot.api.core.service.export.HPPChromosomeReportWriter;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.nextprot.api.core.domain.EntryReportTest.newEntryReport;


@ActiveProfiles({ "dev","cache" })
public class HPPChromosomeReportTSVWriterTest extends CoreUnitBaseTest {

    @Autowired
    private OverviewService overviewService;

    @Test
    public void writeChromosomeReport() throws Exception {

        ChromosomeReport report = new ChromosomeReport();
        report.setDataRelease("2017-01-23");

        ChromosomeReport.Summary summary = new ChromosomeReport.Summary();
        summary.setChromosome("Y");
        summary.setEntryReportCount(58);
        summary.setEntryCount(48);

        report.setSummary(summary);

        EntryReport entryReport1 = newEntryReport("SRY", "NX_Q05066", "Yp11.2",
                "2786855", "2787699", ProteinExistence.UNCERTAIN,
                false, true, true, true, 1, 47, 1,
                "Sex-determining region Y protein");

        EntryReport entryReport2 = newEntryReport("RBMY1C", "NX_P0DJD4", "Yq11.23",
                "-", "-", ProteinExistence.PROTEIN_LEVEL,
                false, true, false, false, 1, 0, 0,
                "RNA-binding motif protein, Y chromosome, family 1 member C");

        EntryReport entryReport3 = newEntryReport("MFSD14A", "NX_Q96MC6", "1p21.2",
                "100038097", "100083377", ProteinExistence.PROTEIN_LEVEL,
                false, false, false, false, 1, 141, 3,
                "Hippocampus abundant transcript 1 protein");

        EntryReport entryReport4 = newEntryReport("VCY", "NX_O14598", "Yq11.221",
                "13985772", "13986513", ProteinExistence.PROTEIN_LEVEL,
                true, true, false, false, 1, 4, 0,
                "Testis-specific basic protein Y 1");

        EntryReport entryReport5 = newEntryReport("VCY", "NX_O14598", "Yq11.221",
                "14056217", "14056958", ProteinExistence.PROTEIN_LEVEL,
                true, true, false, false, 1, 4, 0,
                "Testis-specific basic protein Y 1");

        report.setEntryReports(Arrays.asList(entryReport1, entryReport2, entryReport3, entryReport4, entryReport5));

        StringOutputStream sos = new StringOutputStream();

        HPPChromosomeReportWriter writer = new HPPChromosomeReportTSVWriter(sos, overviewService);
        writer.write(report);

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertEquals(5, observedLines.length);
        Assert.assertEquals("neXtProt AC\tGene name(s)\tProtein existence\tProteomics\tAntibody", observedLines[0]);
        Assert.assertEquals("NX_O14598\tVCY;VCY1B\tEvidence at protein level\tyes\tyes", observedLines[1]);
        Assert.assertEquals("NX_P0DJD4\tRBMY1C\tEvidence at protein level\tno\tyes", observedLines[2]);
        Assert.assertEquals("NX_Q05066\tSRY\tUncertain\tno\tyes", observedLines[3]);
        Assert.assertEquals("NX_Q96MC6\tMFSD14A\tEvidence at protein level\tno\tno", observedLines[4]);

    }

}