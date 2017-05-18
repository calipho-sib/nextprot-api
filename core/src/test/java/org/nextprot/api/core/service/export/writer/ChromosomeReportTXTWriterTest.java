package org.nextprot.api.core.service.export.writer;

import org.codehaus.plexus.util.StringOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomalLocation;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.ProteinExistenceLevel;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;

import java.util.Arrays;
import java.util.Collections;

public class ChromosomeReportTXTWriterTest {

    @Test
    public void writeChromosomeReport() throws Exception {

        ChromosomeReport report = new ChromosomeReport();
        report.setDataRelease("2017-01-23");

        ChromosomeReport.Summary summary = new ChromosomeReport.Summary();
        summary.setChromosome("Y");

        ChromosomeReport.Summary.Count count = new ChromosomeReport.Summary.Count();
        count.setGeneCount(58);
        count.setEntryCount(48);
        summary.setCount(count);

        report.setSummary(summary);

        EntryReport entryReport = new EntryReport();

        ChromosomalLocation cl = new ChromosomalLocation();
        cl.setMasterGeneNames("SRY");
        cl.setFirstPosition(2786855);
        cl.setLastPosition(2787699);
        cl.setChromosome("Y");
        cl.setBand("p11.2");

        entryReport.setAccession("NX_Q05066");
        entryReport.setChromosomalLocation(cl);
        entryReport.setProteinExistence(ProteinExistenceLevel.PROTEIN_LEVEL);
        entryReport.setPropertyTest(EntryReport.IS_PROTEOMICS, false);
        entryReport.setPropertyTest(EntryReport.IS_ANTIBODY, true);
        entryReport.setPropertyTest(EntryReport.IS_3D, true);
        entryReport.setPropertyTest(EntryReport.IS_DISEASE, true);
        entryReport.setPropertyCount(EntryReport.ISOFORM_COUNT, 1);
        entryReport.setPropertyCount(EntryReport.VARIANT_COUNT, 47);
        entryReport.setPropertyCount(EntryReport.PTM_COUNT, 1);
        entryReport.setDescription("Sex-determining region Y protein");

        report.setEntryReports(Collections.singletonList(entryReport));

        StringOutputStream sos = new StringOutputStream();
        ChromosomeReportWriter writer = new ChromosomeReportTXTWriter(sos);
        writer.write(report);

        String[] observedLines = sos.toString().split("\\n");

        Assert.assertTrue(Arrays.stream(observedLines)
                .anyMatch(l -> l.matches("^Gene      neXtProt     Chromosomal  Start    Stop     Protein          Prote- Anti- 3D    Dise- Iso-  Vari-  PTMs Description.*$"))
        );
        Assert.assertTrue(Arrays.stream(observedLines)
                .anyMatch(l -> l.matches("^name      AC           position     position position existence        omics  body        ase   forms ants.+$"))
        );
        Assert.assertTrue(Arrays.stream(observedLines)
                .anyMatch(l -> l.matches("^SRY       NX_Q05066    Yp11.2        2786855  2787699 protein level    no     yes   yes   yes       1    47     1 Sex-determining region Y protein.*$"))
        );
    }

    /*
--------------------------------------------------------------------------------------------------------------------------------------------------------
Gene      neXtProt     Chromosomal Start    Stop     Protein         Prote-Anti- 3D    Dise- Iso- Vari-  PTMs   Description
name      AC           position    position position existence       omics body        ase   formsants
________________________________________________________________________________________________________________________________________________________
SRY       NX_Q05066    Yp11.2        2786855  2787699protein level   no    yes   yes   yes       1     47      1Sex-determining region Y protein

     */

}