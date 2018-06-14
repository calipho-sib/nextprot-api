package org.nextprot.api.core.service.exon;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.SimpleExon;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeneRegionMappingConflictSolverTest {

    @Test
    public void testFixGeneRegions() {

        List<GeneRegion> transcriptExons = mockGeneRegionList(617, 630, 1273, 1360, 20933, 21289, 27229, 27546,
                28147, 28479, 30598, 30711, 33709, 33733, 40549, 40588, 43154, 45731);

        List<GeneRegion> isoformToGeneMappings = mockGeneRegionList(617, 630, 1273, 1360, 20933, 21240, 21244, 21289, 27229, 27546,
                28147, 28479, 30598, 30711, 33709, 33733, 40549, 40588, 43154, 45731);

        List<GeneRegion> geneRegions = new GeneRegionMappingConflictSolver("", wrapToSimpleExonList(transcriptExons),
                isoformToGeneMappings).resolveConflicts();

        assertPositions(geneRegions.get(0), 617,630);
        assertPositions(geneRegions.get(1), 1273,1360);
        assertPositions(geneRegions.get(2), 20933,21240);
        assertPositions(geneRegions.get(3), 21244,21289);
        assertPositions(geneRegions.get(4), 27229,27546);
        assertPositions(geneRegions.get(5), 28147,28479);
        assertPositions(geneRegions.get(6), 30598,30711);
        assertPositions(geneRegions.get(7), 33709,33733);
        assertPositions(geneRegions.get(8), 40549,40588);
        assertPositions(geneRegions.get(9), 43154,45731);
    }

    @Test
    public void testNothingToFixGeneRegionsNX_P46976() {

        List<GeneRegion> transcriptExons = mockGeneRegionList(218, 307, 2802, 2937, 4962, 5136, 5402, 5564, 17936, 18062, 35420, 36291);

        List<GeneRegion> isoformToGeneMappings = mockGeneRegionList(301, 307, 2802, 2937, 4962, 5136, 5402, 5564, 17936, 18062, 35420, 35648);

        List<GeneRegion> geneRegions = new GeneRegionMappingConflictSolver("", wrapToSimpleExonList(transcriptExons),
                isoformToGeneMappings).resolveConflicts();

        assertPositions(geneRegions.get(0), 218,307);
        assertPositions(geneRegions.get(1), 2802,2937);
        assertPositions(geneRegions.get(2), 4962,5136);
        assertPositions(geneRegions.get(3), 5402,5564);
        assertPositions(geneRegions.get(4), 17936,18062);
        assertPositions(geneRegions.get(5), 35420,36291);
    }

    @Test
    public void testFixGeneRegionsNX_P17405_1() {

        List<GeneRegion> transcriptExons = mockGeneRegionList(45, 486, 957, 1726, 2786, 2957, 3187, 3263, 3466, 3611, 3768, 4348);

        List<GeneRegion> isoformToGeneMappings = mockGeneRegionList(169, 276, 283, 486, 957, 1726, 2786, 2957, 3187, 3263, 3466, 3611, 3768, 4174);

        List<GeneRegion> geneRegions = new LowQualityGeneRegionMappingConflictSolver("", wrapToSimpleExonList(transcriptExons),
                isoformToGeneMappings).resolveConflicts();

        assertPositions(geneRegions.get(0), 169,276);
        assertPositions(geneRegions.get(1), 283,486);
        assertPositions(geneRegions.get(2), 957,1726);
        assertPositions(geneRegions.get(3), 2786,2957);
        assertPositions(geneRegions.get(4), 3187,3263);
        assertPositions(geneRegions.get(5), 3466,3611);
        assertPositions(geneRegions.get(6), 3768,4348);
    }

    private static List<GeneRegion> mockGeneRegionList(int... startEnds) {

        Preconditions.checkArgument(startEnds.length % 2 == 0);

        List<GeneRegion> geneRegions = new ArrayList<>();

        for (int i=0 ; i<startEnds.length-1 ; i+=2) {

            GeneRegion geneRegion = mock(GeneRegion.class);

            when(geneRegion.getFirstPosition()).thenReturn(startEnds[i]);
            when(geneRegion.getLastPosition()).thenReturn(startEnds[i+1]);
            when(geneRegion.toString()).thenReturn(startEnds[i]+"-"+startEnds[i+1]);

            geneRegions.add(geneRegion);
        }

        return geneRegions;
    }

    private static List<SimpleExon> wrapToSimpleExonList(List<GeneRegion> list) {

        return list.stream()
                .map(gr -> {
                    SimpleExon se = new SimpleExon();
                    se.setGeneRegion(gr);
                    return se;
                })
                .collect(Collectors.toList());
    }

    private static void assertPositions(GeneRegion observed, int expectedFrom, int expectedTo) {

        Assert.assertEquals(expectedFrom, observed.getFirstPosition());
        Assert.assertEquals(expectedTo, observed.getLastPosition());
    }
}