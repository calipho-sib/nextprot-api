package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.GeneRegion;
import org.nextprot.api.core.domain.exon.UncategorizedExon;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GenericExonCategorizerTest {

    @Test
    public void testFirstExonStatus() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(284, 1956);

        ExonCategory status = categorizer.categorize(mockExonList(134, 286).get(0));
        Assert.assertEquals(ExonCategory.START, status);
    }

    @Test
    public void testInternalExonStatus() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(284, 1956);

        ExonCategory status = categorizer.categorize(mockExonList(996, 1150).get(0));
        Assert.assertEquals(ExonCategory.CODING, status);
    }

    @Test
    public void testMonoExonStatus() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(284, 1956);

        ExonCategory status = categorizer.categorize(mockExonList(184, 2000).get(0));
        Assert.assertEquals(ExonCategory.MONO, status);
    }

    @Test
    public void testStopOnlyExonStatus() throws Exception {

        // NX_Q96M20-3
        ExonCategorizer categorizer = new ExonCategorizer(174, 61767);

        ExonCategory status = categorizer.categorize(mockExonList(61768, 62080).get(0));

        Assert.assertEquals(ExonCategory.STOP_ONLY, status);
    }

    @Test
    public void testStopExonStatus() throws Exception {

        // NX_Q96M20-1
        ExonCategorizer categorizer = new ExonCategorizer(174, 62056);

        ExonCategory status = categorizer.categorize(mockExonList(61768, 62080).get(0));
        Assert.assertEquals(ExonCategory.STOP, status);
    }

    @Test
    public void testStopExonStatus2() throws Exception {

        // NX_Q8NFW8-2
        ExonCategorizer categorizer = new ExonCategorizer(131, 16108);

        ExonCategory status = categorizer.categorize(mockExonList(16108, 16261).get(0));
        Assert.assertEquals(ExonCategory.STOP, status);
    }

    @Test
    public void testNonCodingExon() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(941, 43058);

        ExonCategory status = categorizer.categorize(mockExonList(276, 289).get(0));
        Assert.assertEquals(ExonCategory.NOT_CODING, status);
    }

    @Test(expected = ExonInvalidBoundException.class)
    public void testOutOfBoundExon() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(941, 43058);

        UncategorizedExon exon = mockExonList(276, 28).get(0);
        GeneRegion gr = new GeneRegion();
        gr.setGeneName("roudoudou");
        when(exon.getGeneRegion()).thenReturn(gr);

        categorizer.categorize(exon);
    }

    public static List<UncategorizedExon> mockExonList(int... startEnds) {

        Preconditions.checkArgument(startEnds.length % 2 == 0);

        List<UncategorizedExon> exons = new ArrayList<>();

        for (int i=0 ; i<startEnds.length-1 ; i+=2) {

            UncategorizedExon exon = mock(UncategorizedExon.class);
            GeneRegion geneRegion = mock(GeneRegion.class);
            when(geneRegion.getFirstPosition()).thenReturn(startEnds[i]);
            when(geneRegion.getLastPosition()).thenReturn(startEnds[i+1]);
            when(geneRegion.getGeneName()).thenReturn("roudoudou");

            when(exon.getFirstPositionOnGene()).thenReturn(startEnds[i]);
            when(exon.getLastPositionOnGene()).thenReturn(startEnds[i+1]);
            when(exon.getGeneRegion()).thenReturn(geneRegion);

            exons.add(exon);
        }

        return exons;
    }
}