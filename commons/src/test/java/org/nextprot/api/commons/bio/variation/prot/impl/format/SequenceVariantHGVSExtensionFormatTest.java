package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.ExtensionInitiation;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.ExtensionTermination;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

public class SequenceVariantHGVSExtensionFormatTest {

    private VariantHGVSFormat format = new VariantHGVSFormat();

    @Test
    public void testParseInitiationExtensionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Met1Valext-12");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_INIT, pm.getSequenceChange().getType());
        Assert.assertEquals(-12, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    @Test
    public void testParseTerminationExtensionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Ter110Glnext*17");

        Assert.assertEquals(AminoAcidCode.STOP, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(110, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_TERM, pm.getSequenceChange().getType());
        Assert.assertEquals(17, ((ExtensionTermination)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.GLUTAMINE, ((ExtensionTermination)pm.getSequenceChange()).getValue());
    }

    @Test(expected = java.text.ParseException.class)
    public void shouldNotParseTer() throws Exception {

        format.parse("p.Ter110GlnextTer17");
    }

    @Test
    public void testParseInitiationExtensionCode1() throws Exception {

        SequenceVariation pm = format.parse("p.M1Vext-12");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_INIT, pm.getSequenceChange().getType());
        Assert.assertEquals(-12, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    @Test
    public void testParseTerminationExtensionCode1() throws Exception {

        SequenceVariation pm = format.parse("p.*110Glnext*17");

        Assert.assertEquals(AminoAcidCode.STOP, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(110, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.EXTENSION_TERM, pm.getSequenceChange().getType());
        Assert.assertEquals(17, ((ExtensionTermination)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.GLUTAMINE, ((ExtensionTermination)pm.getSequenceChange()).getValue());
    }

    @Test
    public void testFormatExtensionCode1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-12, AminoAcidCode.VALINE).build();

        Assert.assertEquals("p.M1Vext-12", format.format(pm));
    }

    @Test
    public void testFormatExtensionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-12, AminoAcidCode.VALINE).build();

        Assert.assertEquals("p.Met1Valext-12", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatExtensionTermCode1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.STOP, 110)
                .thenTerminationExtension(17, AminoAcidCode.GLUTAMINE).build();

        Assert.assertEquals("p.*110Qext*17", format.format(pm));
    }

    @Test
    public void testFormatExtensionTermCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.STOP, 110)
                .thenTerminationExtension(17, AminoAcidCode.GLUTAMINE).build();

        Assert.assertEquals("p.Ter110Glnext*17", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }
}