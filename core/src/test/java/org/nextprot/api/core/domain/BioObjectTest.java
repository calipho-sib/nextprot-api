package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;

public class BioObjectTest {

    @Test
    public void testBioEntry() {

        BioObject bioEntry = BioObject.internal(BioObject.BioType.PROTEIN);
        bioEntry.setAccession("NX_P01308");

        Assert.assertEquals("neXtProt", bioEntry.getDatabase());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bioEntry.getBioType());
        Assert.assertEquals("NX_P01308", bioEntry.getAccession());
        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioEntry.getResourceType());
        Assert.assertEquals(1, bioEntry.size());
    }

    @Test
    public void testBioIsoform() {

        BioObject bioIsoform = BioObject.internal(BioObject.BioType.PROTEIN_ISOFORM);
        bioIsoform.setAccession("NX_P01308-1");

        Assert.assertEquals("neXtProt", bioIsoform.getDatabase());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ISOFORM, bioIsoform.getBioType());
        Assert.assertEquals("NX_P01308-1", bioIsoform.getAccession());
        Assert.assertEquals(BioObject.ResourceType.INTERNAL, bioIsoform.getResourceType());
    }

    @Test
    public void testMolecule() {

        BioObject bioExternal = BioObject.external(BioObject.BioType.CHEMICAL, "ChEBI");
        bioExternal.setId(2763273);

        Assert.assertEquals(BioObject.BioType.CHEMICAL, bioExternal.getBioType());
        Assert.assertEquals(BioObject.ResourceType.EXTERNAL, bioExternal.getResourceType());
    }
}