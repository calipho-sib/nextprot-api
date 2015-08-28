package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioObjectExternalTest {

    @Test
    public void testMolecule() {

        BioObjectExternal bioExternal = new BioObjectExternal(BioObject.BioType.CHEMICAL);
        bioExternal.setId(2763273);

        Assert.assertEquals(BioObject.BioType.CHEMICAL, bioExternal.getBioType());
        Assert.assertEquals(BioObject.ResourceType.EXTERNAL, bioExternal.getResourceType());
    }
}