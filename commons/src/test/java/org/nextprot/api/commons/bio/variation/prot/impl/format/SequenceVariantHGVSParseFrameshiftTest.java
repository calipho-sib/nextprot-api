package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.Frameshift;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.text.ParseException;

public class SequenceVariantHGVSParseFrameshiftTest {

    VariantHGVSFormat format = new VariantHGVSFormat();

    @Test
    public void testParseFrameshift() throws Exception {

        SequenceVariation pm = format.parse("p.M682Afs*2");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(682, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.FRAMESHIFT, pm.getSequenceChange().getType());
        Assert.assertEquals(AminoAcidCode.ALANINE, ((Frameshift.Change)pm.getSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Test
    public void testParseFrameshiftCode3() throws Exception {

        SequenceVariation pm = format.parse("p.Met682AlafsTer2");

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(682, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.FRAMESHIFT, pm.getSequenceChange().getType());
        Assert.assertEquals(AminoAcidCode.ALANINE, ((Frameshift.Change)pm.getSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Ignore
    @Test
    public void testParseAANonStandardFrameshift() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        SequenceVariation pm = format.parse("p.S1476fs*>9");

        Assert.assertEquals(AminoAcidCode.SERINE, pm.getVaryingSequence().getFirstAminoAcid());
        Assert.assertEquals(1476, pm.getVaryingSequence().getFirstAminoAcidPos());
        Assert.assertEquals(SequenceChange.Type.FRAMESHIFT, pm.getSequenceChange().getType());
        Assert.assertEquals(9, pm.getSequenceChange().getValue());
    }

    @Test(expected = ParseException.class)
    public void testParseFs1ShouldFailed() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        format.parse("p.Met682AlafsTer1");
    }

    @Ignore
    @Test
    public void testParseAAFsFix() throws Exception {

        format = new VariantHGVSFormat(ParsingMode.PERMISSIVE);
        format.parse("p.E61fs");
    }

    @Test
    public void testParseFrameshiftsVariants() throws ParseException {

        String[] variants = new String[]{
                "p.Gly173Serfs*19",
                "p.Glu23Valfs*17",
                "p.Tyr186Phefs*6",
                "p.Gly2281Alafs*31",
                "p.Ala1711Profs*76",
                "p.Tyr2660Phefs*43",
                "p.Thr1852Hisfs*28",
                "p.Ser1982Argfs*22",
                "p.Gln167Profs*21",
                "p.Lys427Glyfs*4",
                "p.Asn860Ilefs*14",
                "p.Gln1756Profs*74",
                "p.Ile848Serfs*21",
                "p.Ser1982Argfs*22",
                "p.Ala382Profs*23",
                "p.Glu407Glyfs*43",
                "p.Glu513Glyfs*12",
                "p.Gly820Alafs*3",
                "p.Cys64Glyfs*16",
                "p.Arg18Leufs*12",
                "p.Gly261Trpfs*7",
                "p.Gly2313Alafs*31",
                "p.Ala165Metfs*24"};

        for (String variant : variants) {
            format.parse(variant);
        }
    }
}