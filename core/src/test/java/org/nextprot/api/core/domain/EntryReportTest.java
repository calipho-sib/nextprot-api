package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntryReportTest {

    @Test
    public void shouldSortAccordingToPositionUnknownsLast() throws Exception {

        EntryReport.ByGenePosComparator comparator = new EntryReport.ByGenePosComparator();

        List<EntryReport> list = new ArrayList<>();

        EntryReport er1 = newEntryReport("SASS6", "NX_Q6UVJ0", "1p21.2",
                "100083563", "100132955", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, true, false, false, 1, 246, 6,
                "Spindle assembly abnormal protein 6 homolog");

        EntryReport er2 = newEntryReport("ISG15", "NX_P05161", "1p36.33",
                "1001138", "1014541", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, true, false, false, 1, 101, 6,
                "Ubiquitin-like protein ISG15");

        EntryReport er3 = newEntryReport("KDM1A", "NX_O60341", "1p36.12",
                "23019448", "23083689", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, true, false, false, 2, 187, 33,
                "Lysine-specific histone demethylase 1A");

        EntryReport er4 = newEntryReport("NBPF26", "NX_B4DH59", "1q21.1",
                "-", "-", ProteinExistenceLevel.UNCERTAIN,
                false, true, false, false, 1, 0, 0,
                "Neuroblastoma breakpoint family member 26");

        EntryReport er5 = newEntryReport("PGBD5", "NX_Q8N414", "1q42.13",
                "230314482", "230426371", ProteinExistenceLevel.PROTEIN_LEVEL,
                false, true, false, false, 1, 236, 2,
                "PiggyBac transposable element-derived protein 5");

        list.addAll(Arrays.asList(er1, er2, er3, er4, er5));


        list.sort(comparator);

        Assert.assertEquals("NX_P05161", list.get(0).getAccession());
        Assert.assertEquals("NX_O60341", list.get(1).getAccession());
        Assert.assertEquals("NX_Q6UVJ0", list.get(2).getAccession());
        Assert.assertEquals("NX_Q8N414", list.get(3).getAccession());
        Assert.assertEquals("NX_B4DH59", list.get(4).getAccession());
    }

    public static EntryReport newEntryReport(String geneName, String ac, String chromosalPosition,
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