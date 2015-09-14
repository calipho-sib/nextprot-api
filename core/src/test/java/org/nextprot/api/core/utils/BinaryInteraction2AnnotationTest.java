package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.*;

/**
 * Created by fnikitin on 27/08/15.
 */
public class BinaryInteraction2AnnotationTest {

    @Test
    public void testConvertEvidenceToBioEntry()  {

        Interactant interactant = new Interactant();

        interactant.setAccession("NX_P61626");
        interactant.setDatabase("nextProt");
        interactant.setNextprot(true);
        interactant.setXrefId(123L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant);
        Assert.assertEquals("NX_P61626", bo.getAccession());
        Assert.assertEquals("neXtProt", bo.getDatabase());
        Assert.assertEquals(123L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bo.getBioType());
        Assert.assertTrue(bo instanceof BioEntry);
    }

    @Test
    public void testConvertEvidenceToBioIsoform()  {

        Interactant interactant = new Interactant();

        interactant.setAccession("NX_P61626-1");
        interactant.setDatabase("nextProt");
        interactant.setNextprot(true);
        interactant.setXrefId(123L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant);
        Assert.assertEquals("NX_P61626-1", bo.getAccession());
        Assert.assertEquals("neXtProt", bo.getDatabase());
        Assert.assertEquals(123L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN_ISOFORM, bo.getBioType());
        Assert.assertTrue(bo instanceof BioIsoform);
    }

    @Test
    public void testConvertEvidenceToExternalBioEntry()  {

        Interactant interactant = new Interactant();

        interactant.setAccession("Q81LD0");
        interactant.setDatabase("UniProt");
        interactant.setNextprot(false);
        interactant.setXrefId(15642964L);

        BioObject bo = BinaryInteraction2Annotation.newBioObject(interactant);
        Assert.assertEquals("Q81LD0", bo.getAccession());
        Assert.assertEquals("UniProt", bo.getDatabase());
        Assert.assertEquals(15642964L, bo.getId());
        Assert.assertEquals(BioObject.BioType.PROTEIN, bo.getBioType());
        Assert.assertTrue(bo instanceof BioObjectExternal);
    }
}