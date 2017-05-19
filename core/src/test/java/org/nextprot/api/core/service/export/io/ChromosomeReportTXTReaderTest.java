package org.nextprot.api.core.service.export.io;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.ChromosomeReport;

import java.io.StringReader;
import java.text.ParseException;

public class ChromosomeReportTXTReaderTest {

    @Test
    public void shouldReadValidFormat() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "        Geneva bioinformatics (GeneBio) SA; Geneva, Switzerland\n" +
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
                "OR4F29     NX_Q6IEY1    \t1p36.33        450740    451678\ttranscript level\tno    \tyes   \tno    \tno    \t    1      9      2\tOlfactory receptor 4F3/4F16/4F29\n";


        ChromosomeReport chromosomeReport = reader.read(new StringReader(content));

        Assert.assertEquals("2017-04-12", chromosomeReport.getDataRelease());
        Assert.assertEquals("1", chromosomeReport.getSummary().getChromosome());
        Assert.assertEquals(2056, chromosomeReport.getSummary().getEntryCount());
        Assert.assertEquals(2104, chromosomeReport.getSummary().getGeneCount());

        System.out.println(chromosomeReport);
    }

    @Test(expected = ParseException.class)
    public void shouldNotBeAbleToReadInvalidChromosomeName() throws Exception {

        ChromosomeReportTXTReader reader = new ChromosomeReportTXTReader();

        String content = "----------------------------------------------------------------------------\n" +
                "        neXtProt - a comprehensive human-centric discovery platform\n" +
                "        SIB Swiss Institute of Bioinformatics; Geneva, Switzerland\n" +
                "        Geneva bioinformatics (GeneBio) SA; Geneva, Switzerland\n" +
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
                "        Geneva bioinformatics (GeneBio) SA; Geneva, Switzerland\n" +
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
                "        Geneva bioinformatics (GeneBio) SA; Geneva, Switzerland\n" +
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
                "        Geneva bioinformatics (GeneBio) SA; Geneva, Switzerland\n" +
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