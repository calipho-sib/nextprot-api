package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.IsoformEntityName;

/**
 * @author fnikitin
 */
public class NXVelocityUtilsTest {

    @Test
    public void testFormatIsoformId1() throws Exception {

        Isoform isoform = new Isoform();

        Assert.assertEquals("Iso 1", NXVelocityUtils.formatIsoformId(isoform));
    }

    @Test
    public void testFormatIsoformId2() throws Exception {

        Isoform isoform = new Isoform();

        IsoformEntityName isoformEntityName = Mockito.mock(IsoformEntityName.class);
        Mockito.when(isoformEntityName.getValue()).thenReturn("M");

        isoform.setMainEntityName(isoformEntityName);

        Assert.assertEquals("M", NXVelocityUtils.formatIsoformId(isoform));
    }

    @Test
    public void testFormatIsoformId3() throws Exception {

        Isoform isoform = new Isoform();

        IsoformEntityName isoformEntityName = Mockito.mock(IsoformEntityName.class);
        Mockito.when(isoformEntityName.getValue()).thenReturn("2");

        isoform.setMainEntityName(isoformEntityName);

        Assert.assertEquals("Iso 2", NXVelocityUtils.formatIsoformId(isoform));
    }
}