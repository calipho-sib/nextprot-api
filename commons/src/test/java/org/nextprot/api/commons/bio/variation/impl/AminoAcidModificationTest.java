package org.nextprot.api.commons.bio.variation.impl;

import org.junit.Assert;
import org.junit.Test;

public class AminoAcidModificationTest {

    @Test
    public void validAminoAcidModificationNameShouldReturnEnum() throws Exception {

        Assert.assertEquals(AminoAcidModification.ACETYLATION, AminoAcidModification.valueOfAminoAcidModification("Ac"));
    }

    @Test
    public void validAminoAcidModificationAnyCaseNameShouldReturnEnum() throws Exception {

        Assert.assertEquals(AminoAcidModification.ACETYLATION, AminoAcidModification.valueOfAminoAcidModification("ac"));
    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidAminoAcidModificationNameShouldThrowException() throws Exception {

        AminoAcidModification.valueOfAminoAcidModification("spongebob");
    }

    @Test
    public void isValidAminoAcidModification() throws Exception {

        Assert.assertFalse(AminoAcidModification.isValidAminoAcidModification("spongebob"));
    }
}