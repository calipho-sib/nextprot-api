package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.impl.Substitution;

import java.text.ParseException;

public class SequenceVariationHGVSParseSubstitutionTest {

    SequenceVariantHGVSFormat format = new SequenceVariantHGVSFormat();


    @Test(expected = ParseException.class)
    public void testParseUnknownCode1AA() throws Exception {

        format.parse("p._54C");
    }

    @Test(expected = ParseException.class)
    public void testParseUnknownCode3AA() throws Exception {

        format.parse("p.Mat54Trp");
    }

    ///// SUBSTITUTIONS
    @Test
    public void testParseSubstitution() throws Exception {

        SequenceVariation pm = format.parse("p.R54C");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionStop() throws Exception {

        SequenceVariation pm = format.parse("p.R54*");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.STOP, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseSubstitutionCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Arg54Cys");

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testPermissiveParserCorrectlyParseStandardSubstitution() throws Exception {

        SequenceVariation pm = format.parse("p.R54C", SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode1() throws Exception {

        SequenceVariation pm = format.parse("p.*104E", SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.STOP, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.STOP, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.GLUTAMIC_ACID, pm.getSequenceChange().getValue());
    }

    @Test
    public void testParseAATerSubstitutionFixCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Ter104Glu", SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.STOP, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(AminoAcidCode.STOP, pm.getLastChangingAminoAcid());
        Assert.assertEquals(104, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.GLUTAMIC_ACID, pm.getSequenceChange().getValue());
    }

    @Ignore
    @Test
    public void testParseAATerSubstitutionFix5() throws Exception {

        SequenceVariation pm = format.parse("p.Y553_K558>", SequenceVariantHGVSFormat.ParsingMode.PERMISSIVE);
    }
}