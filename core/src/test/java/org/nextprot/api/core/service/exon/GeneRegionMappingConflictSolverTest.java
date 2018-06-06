package org.nextprot.api.core.service.exon;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GeneRegion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeneRegionMappingConflictSolverTest {

    @Test
    public void testFixGeneRegions() {

        List<GeneRegion> transcriptExons = mockGeneRegionList(617, 630, 1273, 1360, 20933, 21289, 27229, 27546,
                28147, 28479, 30598, 30711, 33709, 33733, 40549, 40588, 43154, 45731);

        List<GeneRegion> isoformToGeneMappings = mockGeneRegionList(617, 630, 1273, 1360, 20933, 21240, 21244, 21289, 27229, 27546,
                28147, 28479, 30598, 30711, 33709, 33733, 40549, 40588, 43154, 45731);

        Map<Integer, Integer> transcriptExonsIndices = new HashMap<>();

        List<GeneRegion> geneRegions = new GeneRegionMappingConflictSolver(transcriptExons, isoformToGeneMappings)
                .resolveConflicts(transcriptExonsIndices);

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

        Assert.assertEquals(8, transcriptExonsIndices.size());
    }

    private static List<GeneRegion> mockGeneRegionList(int... startEnds) {

        Preconditions.checkArgument(startEnds.length % 2 == 0);

        List<GeneRegion> geneRegions = new ArrayList<>();

        for (int i=0 ; i<startEnds.length-1 ; i+=2) {

            GeneRegion geneRegion = mock(GeneRegion.class);

            when(geneRegion.getFirstPosition()).thenReturn(startEnds[i]);
            when(geneRegion.getLastPosition()).thenReturn(startEnds[i+1]);

            geneRegions.add(geneRegion);
        }

        return geneRegions;
    }

    private static void assertPositions(GeneRegion observed, int expectedFrom, int expectedTo) {

        Assert.assertEquals(expectedFrom, observed.getFirstPosition());
        Assert.assertEquals(expectedTo, observed.getLastPosition());
    }
}