package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IsoformUtilsTest {

    @Test
    public void testNumericSortIsoNamesIfContainDash() throws Exception {

        List<String> list = Arrays.asList("NX_Q13557-1", "NX_Q13557-10", "NX_Q13557-12", "NX_Q13557-3", "NX_Q13557-9", "NX_Q13557-8");

        Collections.sort(list, new IsoformUtils.ByIsoformUniqueNameComparator());

        Assert.assertEquals(Arrays.asList("NX_Q13557-1", "NX_Q13557-3", "NX_Q13557-8", "NX_Q13557-9", "NX_Q13557-10", "NX_Q13557-12"), list);
    }

    @Test
    public void testLexicalSortIsoNamesIfNoDash() throws Exception {

        List<String> list = Arrays.asList("NX_Q13557_1", "NX_Q13557_10", "NX_Q13557_12", "NX_Q13557_3", "NX_Q13557_9", "NX_Q13557_8");

        Collections.sort(list, new IsoformUtils.ByIsoformUniqueNameComparator());

        Assert.assertEquals(Arrays.asList("NX_Q13557_1", "NX_Q13557_10", "NX_Q13557_12", "NX_Q13557_3", "NX_Q13557_8", "NX_Q13557_9"), list);
    }

    @Test
    public void testSort() throws Exception {

        List<String> list = Arrays.asList("NX_Q13557-1", "NX_Q13557-10", "NX_Q13557-12", "NX_Q13557-3", "NX_Q13557-9", "NX_Q13557-8", "NX_P01306-1", "NX_P01306-2");

        Collections.sort(list, new IsoformUtils.ByIsoformUniqueNameComparator());

        Assert.assertEquals(Arrays.asList("NX_P01306-1", "NX_P01306-2", "NX_Q13557-1", "NX_Q13557-3", "NX_Q13557-8", "NX_Q13557-9", "NX_Q13557-10", "NX_Q13557-12"), list);
    }
}