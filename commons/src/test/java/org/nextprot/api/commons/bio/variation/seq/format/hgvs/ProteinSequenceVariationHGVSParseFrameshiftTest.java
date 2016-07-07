package org.nextprot.api.commons.bio.variation.seq.format.hgvs;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.Frameshift;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;

import java.text.ParseException;

public class ProteinSequenceVariationHGVSParseFrameshiftTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Test
    public void testParseFrameshift() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.M682Afs*2");

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(AminoAcidCode.Alanine, ((Frameshift.Change)pm.getProteinSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getProteinSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Test
    public void testParseFrameshiftCode3() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.Met682AlafsTer2");

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(AminoAcidCode.Alanine, ((Frameshift.Change)pm.getProteinSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getProteinSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Ignore
    @Test
    public void testParseAANonStandardFrameshift() throws Exception {

        ProteinSequenceVariation pm = format.parse("p.S1476fs*>9", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);

        Assert.assertEquals(AminoAcidCode.Serine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1476, pm.getFirstChangingAminoAcidPos());
        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(9, pm.getProteinSequenceChange().getValue());
    }

    @Ignore
    @Test
    public void testParseAAFsFix() throws Exception {

        format.parse("p.E61fs", ProteinSequenceVariationHGVSFormat.ParsingMode.PERMISSIVE);
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