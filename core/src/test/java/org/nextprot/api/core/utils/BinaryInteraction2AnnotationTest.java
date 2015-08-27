package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.Interactant;

/**
 * Created by fnikitin on 27/08/15.
 */
public class BinaryInteraction2AnnotationTest {

    @Test
    public void testConvertEvidenceToBioEntry()  {

        Interactant interactant = new Interactant();

        interactant.setAccession("NX_P61626");
        interactant.setDatabase("nextProt");
        interactant.setXrefId(123L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant);
        Assert.assertEquals("NX_P61626", bo.getAccession());
        Assert.assertEquals("nextProt", bo.getDatabase());
        Assert.assertEquals(123L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ENTRY, bo.getBioType());
    }

    @Test
    public void testConvertEvidenceToBioIsoform()  {

        Interactant interactant = new Interactant();

        interactant.setAccession("NX_P61626-1");
        interactant.setDatabase("nextProt");
        interactant.setXrefId(123L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant);
        Assert.assertEquals("NX_P61626-1", bo.getAccession());
        Assert.assertEquals("nextProt", bo.getDatabase());
        Assert.assertEquals(123L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ISOFORM, bo.getBioType());
    }
}