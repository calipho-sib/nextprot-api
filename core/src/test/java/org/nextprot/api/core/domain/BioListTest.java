package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by fnikitin on 26/08/15.
 */
public class BioListTest {

    @Test
    public void testComplex() {

        BioComplex bioComplex = new BioComplex(new BioIsoform(), new BioIsoform(), new BioObjectExternal(BioObject.BioType.CHEMICAL));

        Assert.assertEquals(3, bioComplex.size());
        Assert.assertEquals(BioObject.BioType.COMPLEX, bioComplex.getBioType());
        Assert.assertEquals(BioObject.ResourceType.MIXED, bioComplex.getResourceType());
    }

    @Test
    public void testGroup() {

        BioGroup bioGroup = new BioGroup(new BioIsoform(), new BioIsoform(), new BioObjectExternal(BioObject.BioType.CHEMICAL));

        Assert.assertEquals(3, bioGroup.size());
        Assert.assertEquals(BioObject.BioType.GROUP, bioGroup.getBioType());
        Assert.assertEquals(BioObject.ResourceType.MIXED, bioGroup.getResourceType());
    }
}