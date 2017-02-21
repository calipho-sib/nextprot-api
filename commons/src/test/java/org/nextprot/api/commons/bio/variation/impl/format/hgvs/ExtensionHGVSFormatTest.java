package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.impl.ExtensionInitiation;
import org.nextprot.api.commons.bio.variation.impl.ExtensionTermination;

public class ExtensionHGVSFormatTest {

    SequenceVariationHGVSFormat format = new SequenceVariationHGVSFormat();

    @Test
    public void testFormatInitiationExtensionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Met1Valext-12");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_INIT, pm.getSequenceChange().getType());
        Assert.assertEquals(-12, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    @Test
    public void testFormatTerminationExtensionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Ter110GlnextTer17");

        Assert.assertEquals(AminoAcidCode.STOP, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(110, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_TERM, pm.getSequenceChange().getType());
        Assert.assertEquals(17, ((ExtensionTermination)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.GLUTAMINE, ((ExtensionTermination)pm.getSequenceChange()).getValue());
    }

    @Test
    public void testFormatInitiationExtensionCode1() throws Exception {

        SequenceVariation pm = format.parse("p.M1Vext-12");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_INIT, pm.getSequenceChange().getType());
        Assert.assertEquals(-12, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    @Test
    public void testFormatTerminationExtensionCode1() throws Exception {

        SequenceVariation pm = format.parse("p.*110Glnext*17");

        Assert.assertEquals(AminoAcidCode.STOP, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(110, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_TERM, pm.getSequenceChange().getType());
        Assert.assertEquals(17, ((ExtensionTermination)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.GLUTAMINE, ((ExtensionTermination)pm.getSequenceChange()).getValue());
    }
}