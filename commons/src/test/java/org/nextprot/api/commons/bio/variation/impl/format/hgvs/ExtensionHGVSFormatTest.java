package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.impl.InitiationExtension;

public class ExtensionHGVSFormatTest {

    SequenceVariationHGVSFormat format = new SequenceVariationHGVSFormat();

    @Test
    public void testFormatInitiationExtensionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Met1Valext-12");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_INIT, pm.getSequenceChange().getType());
        Assert.assertEquals(-12, ((InitiationExtension)pm.getSequenceChange()).getNewUpstreamInitPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((InitiationExtension)pm.getSequenceChange()).getValue());
    }
}