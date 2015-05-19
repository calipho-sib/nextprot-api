package org.nextprot.api.core.utils.peff;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 19/05/15.
 */
public class ValueTest {

    @Test
    public void testUnknown() throws Exception {

        Location.Value location = Location.Value.Unknown();

        Assert.assertEquals(0, location.getValue());
        Assert.assertTrue(!location.isDefined());
    }

    @Test
    public void testUnknown2() throws Exception {

        Location.Value location = Location.Value.of(-1);

        Assert.assertEquals(0, location.getValue());
        Assert.assertTrue(!location.isDefined());
    }

    @Test
    public void testOf() throws Exception {

        Location.Value location = Location.Value.of(12);

        Assert.assertEquals(12, location.getValue());
        Assert.assertTrue(location.isDefined());
    }
}