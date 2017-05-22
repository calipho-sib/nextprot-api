package org.nextprot.api.core.service.export.io;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;

import java.text.ParseException;
import java.util.Arrays;

public class ChromosomeReportTXTWriterTest {

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
                "2786855", "2787699", ProteinExistenceLevel.PROTEIN_LEVEL,
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

        report.setEntryReports(Arrays.asList(entryReport1, entryReport2, entryReport3));

        StringOutputStream sos = new StringOutputStream();
        ChromosomeReportWriter writer = new ChromosomeReportTXTWriter(sos);
        writer.write(report);

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertEquals(30, observedLines.length);

        Assert.assertTrue(observedLines[17].matches("^Gene      neXtProt     Chromosomal  Start      Stop       Protein          Prote- Anti- 3D    Dise- Iso-  Vari-  PTMs Description$"));
        Assert.assertTrue(observedLines[18].matches("^name      AC           position     position   position   existence        omics  body        ase   forms ants\\s+$"));
        Assert.assertTrue(observedLines[20].matches("^SRY       NX_Q05066    Yp11.2          2786855    2787699 protein level    no     yes   yes   yes       1    47     1 Sex-determining region Y protein$"));
        Assert.assertTrue(observedLines[21].matches("^RBMY1C    NX_P0DJD4    Yq11.23               -          - protein level    no     yes   no    no        1     0     0 RNA-binding motif protein, Y chromosome, family 1 member C$"));
        Assert.assertTrue(observedLines[22].matches("^MFSD14A   NX_Q96MC6    1p21.2        100038097  100083377 protein level    no     no    no    no        1   141     3 Hippocampus abundant transcript 1 protein$"));
    }

    private static EntryReport newEntryReport(String geneName, String ac, String chromosalPosition,
                                              String startPos, String stopPos, ProteinExistenceLevel protExistence,
                                              boolean isProteomics, boolean  isAntibody, boolean  is3D, boolean  isDisease,
                                              int isoformCount, int  variantCount, int  ptmCount, String  description) throws ParseException {

        EntryReport entryReport = new EntryReport();

        ChromosomalLocation cl = ChromosomalLocation.fromString(chromosalPosition);
        cl.setRecommendedName(geneName);
        cl.setFirstPosition((startPos.equals("-"))?0:Integer.parseInt(startPos));
        cl.setLastPosition((stopPos.equals("-"))?0:Integer.parseInt(stopPos));

        entryReport.setAccession(ac);
        entryReport.setChromosomalLocation(cl);
        entryReport.setProteinExistence(protExistence);
        entryReport.setPropertyTest(EntryReport.IS_PROTEOMICS, isProteomics);
        entryReport.setPropertyTest(EntryReport.IS_ANTIBODY, isAntibody);
        entryReport.setPropertyTest(EntryReport.IS_3D, is3D);
        entryReport.setPropertyTest(EntryReport.IS_DISEASE, isDisease);
        entryReport.setPropertyCount(EntryReport.ISOFORM_COUNT, isoformCount);
        entryReport.setPropertyCount(EntryReport.VARIANT_COUNT, variantCount);
        entryReport.setPropertyCount(EntryReport.PTM_COUNT, ptmCount);
        entryReport.setDescription(description);
        
        return entryReport;
    }
}