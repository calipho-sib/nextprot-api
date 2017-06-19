package org.nextprot.api.core.service.export.io;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;
import org.nextprot.api.core.domain.EntryReport;

import java.io.StringReader;
import java.text.ParseException;

public class ChromosomeReportTXTReaderTest {

    @Test
    public void shouldReadValidFormat() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "Description: Chromosome 1 report\n" +
                "Name:        nextprot_chromosome_1\n" +
                "Release:     2017-04-12\n" +
                "\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "This file lists all neXtProt entries on chromosome 1\n" +
                "Total number of entries: 2056\n" +
                "Total number of genes: 2104\n" +
                "\n" +
                "--------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "Gene       neXtProt     \tChromosomal  Start    Stop    \tProtein\t\t\tProte-\tAnti-\t3D    \tDise-\tIso-\tVari-\tPTMs\tDescription\n" +
                "name       AC           \tposition     position position\texistence\t\tomics\tbody\t    \tase\tforms\tants\n" +
                "________________________________________________________________________________________________________________________________________________________\n" +
                "OR4F5      NX_Q8NH21    \t1p36.33         69091     70008\thomology        \tno    \tno    \tno    \tno    \t    1     73      1\tOlfactory receptor 4F5\n" +
                "OR4F29     NX_Q6IEY1    \t1p36.33        450740    451678\ttranscript level\tno    \tyes   \tno    \tno    \t    1      9      2\tOlfactory receptor 4F3/4F16/4F29\n" +
                "-          NX_P0DMU3    \t1p36.13             -         -\thomology        \tno    \tyes   \tno    \tno    \t    1      0      0\tFAM231A/C-like protein LOC102723383\n" +
                "HNRNPCL1   NX_O60812    \t-            12847408  12848725\tprotein level   \tyes   \tno    \tno    \tno    \t    1    322      5\tHeterogeneous nuclear ribonucleoprotein C-like 1\n";

        ChromosomeReport chromosomeReport = reader.read(new StringReader(content));

        Assert.assertEquals("2017-04-12", chromosomeReport.getDataRelease());
        Assert.assertEquals("1", chromosomeReport.getSummary().getChromosome());
        Assert.assertEquals(2056, chromosomeReport.getSummary().getEntryCount());
        Assert.assertEquals(2104, chromosomeReport.getSummary().getEntryReportCount());

        Assert.assertEquals(4, chromosomeReport.getEntryReports().size());
        assertEquals(chromosomeReport.getEntryReports().get(0), "OR4F5", "NX_Q8NH21", "1p36.33", "69091", "70008", "homology", false, false, false, false, 1, 73, 1, "Olfactory receptor 4F5");
        assertEquals(chromosomeReport.getEntryReports().get(1), "OR4F29", "NX_Q6IEY1", "1p36.33", "450740", "451678", "transcript level", false, true, false, false, 1, 9, 2, "Olfactory receptor 4F3/4F16/4F29");
        assertEquals(chromosomeReport.getEntryReports().get(2), "-", "NX_P0DMU3", "1p36.13", "-", "-", "homology", false, true, false, false, 1, 0, 0, "FAM231A/C-like protein LOC102723383");
        assertEquals(chromosomeReport.getEntryReports().get(3), "HNRNPCL1", "NX_O60812", "1", "12847408", "12848725", "protein level", true, false, false, false, 1, 322, 5, "Heterogeneous nuclear ribonucleoprotein C-like 1");
    }

    private void assertEquals(EntryReport entryReport, String expectedGeneName, String expectedAC, String expectedChromosomalPosition,
                              String expectedStartPos, String expectedStopPos, String expectedProtExistence,
                              boolean expectedIsProteomics, boolean expectedIsAntibody, boolean expectedIs3D, boolean expectedIsDisease,
                              int expectedIsoformCount, int expectedVariantCount, int expectedPTMCount, String expectedDescription) {

        Assert.assertEquals(expectedGeneName, entryReport.getGeneName());
        Assert.assertEquals(expectedAC, entryReport.getAccession());
        Assert.assertEquals(expectedChromosomalPosition, entryReport.getChromosomalLocation());
        Assert.assertEquals(expectedStartPos, entryReport.getGeneStartPosition());
        Assert.assertEquals(expectedStopPos, entryReport.getGeneEndPosition());
        Assert.assertEquals(expectedProtExistence, entryReport.getProteinExistence());
        Assert.assertEquals(expectedIsProteomics, entryReport.isProteomics());
        Assert.assertEquals(expectedIsAntibody, entryReport.isAntibody());
        Assert.assertEquals(expectedIs3D, entryReport.is3D());
        Assert.assertEquals(expectedIsDisease, entryReport.isDisease());
        Assert.assertEquals(expectedIsoformCount, entryReport.countIsoforms());
        Assert.assertEquals(expectedVariantCount, entryReport.countVariants());
        Assert.assertEquals(expectedPTMCount, entryReport.countPTMs());
        Assert.assertEquals(expectedDescription, entryReport.getDescription());
    }


    @Test(expected = ParseException.class)
    public void shouldNotBeAbleToReadInvalidChromosomeName() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "Description: Kromosome 1 report\n" +
                "Name:        nextprot_chromosome_1\n" +
                "Release:     2017-04-12\n";

        reader.read(new StringReader(content));
    }

    @Test(expected = ParseException.class)
    public void shouldNotBeAbleToReadInvalidReleaseDate() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "Description: chromosome 1 report\n" +
                "Name:        nextprot_chromosome_1\n" +
                "RELEASE:     2017-04-12\n";

        reader.read(new StringReader(content));
    }

    @Test(expected = ParseException.class)
    public void shouldNotBeAbleToReadNumberOfEntries() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "Description: Chromosome 1 report\n" +
                "Name:        nextprot_chromosome_1\n" +
                "Release:     2017-04-12\n" +
                "\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "This file lists all neXtProt entries on chromosome 1\n" +
                "Total count of entries: 2056\n" +
                "Total number of genes: 2104\n";
        reader.read(new StringReader(content));
    }

    @Test(expected = ParseException.class)
    public void shouldNotBeAbleToReadNumberOfGenes() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "Description: Chromosome 1 report\n" +
                "Name:        nextprot_chromosome_1\n" +
                "Release:     2017-04-12\n" +
                "\n" +
                "----------------------------------------------------------------------------\n" +
                "\n" +
                "This file lists all neXtProt entries on chromosome 1\n" +
                "Total number of entries: 2056\n" +
                "Total count of genes: 2104\n";
        reader.read(new StringReader(content));
    }
}