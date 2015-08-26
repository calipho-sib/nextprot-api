package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioIsoformTest {

    @Test
    public void test() {

        BioIsoform bioIsoform = new BioIsoform();
        bioIsoform.setAccession("NX_P01308-1");

        Assert.assertEquals("neXtProt", bioIsoform.getDatabase());
        Assert.assertEquals(BioObject.BioType.ISOFORM, bioIsoform.getBioType());
        Assert.assertEquals("NX_P01308-1", bioIsoform.getAccession());
        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioIsoform.getResourceType());
    }
}