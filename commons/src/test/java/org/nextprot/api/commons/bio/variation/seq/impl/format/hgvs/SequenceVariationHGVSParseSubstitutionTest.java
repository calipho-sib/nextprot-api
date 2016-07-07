package org.nextprot.api.commons.bio.variation.seq.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.impl.Substitution;

import java.text.ParseException;

public class SequenceVariationHGVSParseSubstitutionTest {

    SequenceVariationHGVSFormat format = new SequenceVariationHGVSFormat();


    @Test(expected = ParseException.class)
    public void testParseUnknownCode1AA() throws Exception {

        format.parse("p.B54C");
    }

    @Test(expected = ParseException.class)
    public void testParseUnknownCode3AA() throws Exception {

        format.parse("p.Mat54Trp");
    }

    ///// SUBSTITUTIONS
    @Test
    public void testParseSubstitution() throws Exception {

        SequenceVariation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionStop() throws Exception {

        SequenceVariation pm = format.parse("p.R54*");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Stop, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Arg54Cys");

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getSequenceChange().getValue());
    }

    @Test
    public void testPermissiveParserCorrectlyParseStandardSubstitution() throws Exception {

        SequenceVariation pm = format.parse("p.R54C", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode1() throws Exception {

        SequenceVariation pm = format.parse("p.*104E", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Stop, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Stop, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Ter104Glu", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Stop, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.Stop, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.GlutamicAcid, pm.getSequenceChange().getValue());
    }

    @Ignore
    @Test
    public void testParseAATerSubstitutionFix5() throws Exception {

        SequenceVariation pm = format.parse("p.Y553_K558>", SequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
    }
}