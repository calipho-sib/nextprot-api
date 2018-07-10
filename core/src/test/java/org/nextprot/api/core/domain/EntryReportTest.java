package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.nextprot.api.core.domain.EntryReportStats.*;

public class EntryReportTest {

    @Test
    public void shouldSortAccordingToPositionUnknownsLast() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
            newEntryReport("SASS6", "NX_Q6UVJ0", "1p21.2",
                    "100083563", "100132955", ProteinExistence.PROTEIN_LEVEL,
                    false, true, false, false, 1, 246, 6,
                    "Spindle assembly abnormal protein 6 homolog"),
            newEntryReport("ISG15", "NX_P05161", "1p36.33",
                    "1001138", "1014541", ProteinExistence.PROTEIN_LEVEL,
                    false, true, false, false, 1, 101, 6,
                    "Ubiquitin-like protein ISG15"),
            newEntryReport("KDM1A", "NX_O60341", "1p36.12",
                    "23019448", "23083689", ProteinExistence.PROTEIN_LEVEL,
                    false, true, false, false, 2, 187, 33,
                    "Lysine-specific histone demethylase 1A"),
            newEntryReport("NBPF26", "NX_B4DH59", "1q21.1",
                    "-", "-", ProteinExistence.UNCERTAIN,
                    false, true, false, false, 1, 0, 0,
                    "Neuroblastoma breakpoint family member 26"),
            newEntryReport("PGBD5", "NX_Q8N414", "1q42.13",
                    "230314482", "230426371", ProteinExistence.PROTEIN_LEVEL,
                    false, true, false, false, 1, 236, 2,
                    "PiggyBac transposable element-derived protein 5")
        );

        list.sort(comparator);

        Assert.assertEquals("NX_P05161", list.get(0).getAccession());
        Assert.assertEquals("NX_O60341", list.get(1).getAccession());
        Assert.assertEquals("NX_Q6UVJ0", list.get(2).getAccession());
        Assert.assertEquals("NX_Q8N414", list.get(3).getAccession());
        Assert.assertEquals("NX_B4DH59", list.get(4).getAccession());
    }

    @Test
    public void shouldSortAccordingToPositionUnknownsBandLast() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
            newEntryReport("HNRNPCL1", "NX_O60812", "1",
                    "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                    true, false, false, false, 1, 322, 5,
                    "Heterogeneous nuclear ribonucleoprotein C-like 1"),
            newEntryReport("HNRNPCL1", "NX_O60812", "1p36.21",
                    "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                    true, false, false, false, 1, 322, 5,
                    "Heterogeneous nuclear ribonucleoprotein C-like 1")
        );

        list.sort(comparator);

        Assert.assertEquals("1p36.21", list.get(0).getChromosomalLocation());
        Assert.assertEquals("1", list.get(1).getChromosomalLocation());
    }

    @Test
    public void shouldSortAccordingToPositionOfBands() throws Exception {

        Comparator<EntryReport> comparator = EntryReport.newByChromosomalPositionComparator();

        List<EntryReport> list = Arrays.asList(
                newEntryReport("HNRNPCL1", "NX_O60812", "1q32",
                        "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1p21.1",
                        "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1q13.4",
                        "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1p1.2-p1.4",
                        "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1"),
                newEntryReport("HNRNPCL1", "NX_O60812", "1p36.21",
                        "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                        true, false, false, false, 1, 322, 5,
                        "Heterogeneous nuclear ribonucleoprotein C-like 1")
        );

        list.sort(comparator);

        Assert.assertEquals("1p36.21", list.get(0).getChromosomalLocation());
        Assert.assertEquals("1p21.1", list.get(1).getChromosomalLocation());
        Assert.assertEquals("1p1.2-p1.4", list.get(2).getChromosomalLocation());
        Assert.assertEquals("1q13.4", list.get(3).getChromosomalLocation());
        Assert.assertEquals("1q32", list.get(4).getChromosomalLocation());
    }

    @Test
    public void testPublicationCounts() throws Exception {

        EntryReport er = newEntryReport("HNRNPCL1", "NX_O60812", "1q32",
                "12847408", "12848725", ProteinExistence.PROTEIN_LEVEL,
                true, false, false, false, 1, 322, 5,
                "Heterogeneous nuclear ribonucleoprotein C-like 1");

        er.setPropertyCount(CURATED_PUBLICATION_COUNT, 44);
        er.setPropertyCount(ADDITIONAL_PUBLICATION_COUNT, 13);
        er.setPropertyCount(PATENT_COUNT, 0);
        er.setPropertyCount(SUBMISSION_COUNT, 3);
        er.setPropertyCount(WEB_RESOURCE_COUNT, 0);

        Assert.assertEquals(44, er.countCuratedPublications());
        Assert.assertEquals(13, er.countAdditionalPublications());
        Assert.assertEquals(0, er.countPatents());
        Assert.assertEquals(3, er.countSubmissions());
        Assert.assertEquals(0, er.countWebResources());
    }

    public static EntryReport newEntryReport(String geneName, String ac, String chromosalPosition,
                                             String startPos, String stopPos, ProteinExistence protExistence,
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
        entryReport.setPropertyTest(IS_PROTEOMICS, isProteomics);
        entryReport.setPropertyTest(IS_ANTIBODY, isAntibody);
        entryReport.setPropertyTest(IS_3D, is3D);
        entryReport.setPropertyTest(IS_DISEASE, isDisease);
        entryReport.setPropertyCount(ISOFORM_COUNT, isoformCount);
        entryReport.setPropertyCount(VARIANT_COUNT, variantCount);
        entryReport.setPropertyCount(PTM_COUNT, ptmCount);
        entryReport.setDescription(description);

        return entryReport;
    }
}