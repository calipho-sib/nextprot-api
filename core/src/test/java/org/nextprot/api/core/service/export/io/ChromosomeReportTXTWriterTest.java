package org.nextprot.api.core.service.export.io;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistence;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;

import java.util.Arrays;

import static org.nextprot.api.core.domain.EntryReportTest.newEntryReport;

public class ChromosomeReportTXTWriterTest {

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
                "2786855", "2787699", ProteinExistence.PROTEIN_LEVEL,
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

        report.setEntryReports(Arrays.asList(entryReport1, entryReport2, entryReport3));

        StringOutputStream sos = new StringOutputStream();
        ChromosomeReportWriter writer = new ChromosomeReportTXTWriter(sos);
        writer.write(report);

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertEquals(29, observedLines.length);

        Assert.assertEquals("Gene          neXtProt      Chromosomal  Start      Stop       Coding  Protein          Prote- Anti- 3D    Dise- Iso-  Vari-  PTMs Description", observedLines[17]);
        Assert.assertEquals("name          AC            location     position   position   strand  existence        omics  body        ase   forms ants        ", observedLines[18]);
        Assert.assertEquals("SRY           NX_Q05066     Yp11.2          2786855    2787699 -       protein level    no     yes   yes   yes       1    47     1 Sex-determining region Y protein", observedLines[20]);
        Assert.assertEquals("RBMY1C        NX_P0DJD4     Yq11.23               -          - -       protein level    no     yes   no    no        1     0     0 RNA-binding motif protein, Y chromosome, family 1 member C", observedLines[21]);
        Assert.assertEquals("MFSD14A       NX_Q96MC6     1p21.2        100038097  100083377 -       protein level    no     no    no    no        1   141     3 Hippocampus abundant transcript 1 protein", observedLines[22]);
    }
}