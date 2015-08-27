package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioEntryTest {

    @Test
    public void test() {

        BioEntry bioEntry = new BioEntry();
        bioEntry.setAccession("NX_P01308");

        Assert.assertEquals("neXtProt", bioEntry.getDatabase());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ENTRY, bioEntry.getBioType());
        Assert.assertEquals("NX_P01308", bioEntry.getAccession());
        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioEntry.getResourceType());
    }
}