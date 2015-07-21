package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.Exon;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by fnikitin on 21/07/15.
 */
public class ExonCategorizerTest {

    @Test
    public void testFirstExonStatus() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(284, 1956);

        ExonType status = categorizer.categorize(createMockExonList(134, 286).get(0));
        Assert.assertEquals(ExonType.START, status);
    }

    @Test
    public void testInternalExonStatus() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(284, 1956);

        ExonType status = categorizer.categorize(createMockExonList(996, 1150).get(0));
        Assert.assertEquals(ExonType.CODING, status);
    }

    @Test
    public void testMonoExonStatus() throws Exception {

        ExonCategorizer categorizer = new ExonCategorizer(284, 1956);

        ExonType status = categorizer.categorize(createMockExonList(184, 2000).get(0));
        Assert.assertEquals(ExonType.MONO, status);
    }

    @Test
    public void testStopOnlyExonStatus() throws Exception {

        // NX_Q96M20-3
        ExonCategorizer categorizer = new ExonCategorizer(174, 61767);

        ExonType status = categorizer.categorize(createMockExonList(61768, 62080).get(0));

        Assert.assertEquals(ExonType.STOP_ONLY, status);
    }

    @Test
    public void testStopExonStatus() throws Exception {

        // NX_Q96M20-1
        ExonCategorizer categorizer = new ExonCategorizer(174, 62056);

        ExonType status = categorizer.categorize(createMockExonList(61768, 62080).get(0));
        Assert.assertEquals(ExonType.STOP, status);
    }

    private List<Exon> createMockExonList(int... startEnds) {

        Preconditions.checkArgument(startEnds.length % 2 == 0);

        List<Exon> exons = new ArrayList<>();

        for (int i=0 ; i<startEnds.length-1 ; i+=2) {

            Exon exon = mock(Exon.class);

            when(exon.getFirstPositionOnGene()).thenReturn(startEnds[i]);
            when(exon.getLastPositionOnGene()).thenReturn(startEnds[i+1]);

            exons.add(exon);
        }

        return exons;
    }
}