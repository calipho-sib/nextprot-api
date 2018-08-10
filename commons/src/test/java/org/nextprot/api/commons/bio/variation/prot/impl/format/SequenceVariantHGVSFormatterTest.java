package org.nextprot.api.commons.bio.variation.prot.impl.format;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.ParsingMode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChangeHGVSFormat;

import java.util.Collection;

public class SequenceVariantHGVSFormatterTest {

    SequenceVariantHGVSFormat format = new SequenceVariantHGVSFormat();

    @Test
    public void testConstrFormat() throws Exception {

        Collection<SequenceChange.Type> types = format.getAvailableChangeTypes();
        Assert.assertEquals(8, types.size());
    }

    @Test
    public void testGetFormat() throws Exception {

        SequenceChangeHGVSFormat fmt = format.getSequenceChangeFormat(SequenceChange.Type.SUBSTITUTION);
        Assert.assertTrue(fmt.matchesWithMode("p.R54C", ParsingMode.STRICT));
    }

    @Test
    public void testFormatSubstitution() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.ARGININE, 54).thenSubstituteWith(AminoAcidCode.CYSTEINE).build();

        Assert.assertEquals("p.R54C", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionWithStop() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.GLUTAMIC_ACID, 31).thenSubstituteWith(AminoAcidCode.STOP).build();

        Assert.assertEquals("p.E31*", format.format(pm));
    }

    @Test
    public void testFormatAADeletion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.LYSINE, 73).thenDelete().build();

        Assert.assertEquals("p.K73del", format.format(pm));
    }

    @Test
    public void testFormatRangeDeletion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LYSINE, 487, AminoAcidCode.LEUCINE, 498).thenDelete().build();

        Assert.assertEquals("p.K487_L498del", format.format(pm));
    }

    @Test
    public void testFormatFrameshift() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 682).thenFrameshift(AminoAcidCode.ALANINE, 2).build();

        Assert.assertEquals("p.M682Afs*2", format.format(pm));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.THREONINE, 399).thenDeleteAndInsert(AminoAcidCode.LEUCINE).build();

        Assert.assertEquals("p.T399delinsL", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LEUCINE, 330, AminoAcidCode.ALANINE, 331).thenDeleteAndInsert(AminoAcidCode.PHENYLALANINE).build();

        Assert.assertEquals("p.L330_A331delinsF", format.format(pm));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMulti() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.ASPARTIC_ACID, 419, AminoAcidCode.ARGININE, 420).thenDeleteAndInsert(AminoAcidCode.SERINE, AminoAcidCode.SERINE, AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.GLYCINE).build();

        Assert.assertEquals("p.D419_R420delinsSSDG", format.format(pm));
    }


    @Test
    public void testFormatSubstitutionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.ARGININE, 54).thenSubstituteWith(AminoAcidCode.CYSTEINE).build();

        Assert.assertEquals("p.Arg54Cys", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatAADeletionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.LYSINE, 73).thenDelete().build();

        Assert.assertEquals("p.Lys73del", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatRangeDeletionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LYSINE, 487, AminoAcidCode.LEUCINE, 498).thenDelete().build();

        Assert.assertEquals("p.Lys487_Leu498del", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatFrameshiftCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 682).thenFrameshift(AminoAcidCode.ALANINE, 2).build();

        Assert.assertEquals("p.Met682AlafsTer2", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletion1AaAndInsertion1Code3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.THREONINE, 399).thenDeleteAndInsert(AminoAcidCode.LEUCINE).build();

        Assert.assertEquals("p.Thr399delinsLeu", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAasAndInsertion1Code3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.LEUCINE, 330, AminoAcidCode.ALANINE, 331).thenDeleteAndInsert(AminoAcidCode.PHENYLALANINE).build();

        Assert.assertEquals("p.Leu330_Ala331delinsPhe", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDeletionMultiAndInsertionMultiCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.ASPARTIC_ACID, 419, AminoAcidCode.ARGININE, 420).thenDeleteAndInsert(AminoAcidCode.SERINE, AminoAcidCode.SERINE, AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.GLYCINE).build();

        Assert.assertEquals("p.Asp419_Arg420delinsSerSerAspGly", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatSubstitutionFixCode1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.STOP, 104).thenSubstituteWith(AminoAcidCode.GLUTAMIC_ACID).build();

        Assert.assertEquals("p.*104E", format.format(pm));
    }

    @Test
    public void testFormatSubstitutionFixCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.STOP, 104).thenSubstituteWith(AminoAcidCode.GLUTAMIC_ACID).build();

        Assert.assertEquals("p.Ter104Glu", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatInsertionCode1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.CYSTEINE, 136, AminoAcidCode.ALANINE, 137).thenInsert(AminoAcidCode.GLYCINE, AminoAcidCode.METHIONINE).build();

        Assert.assertEquals("p.C136_A137insGM", format.format(pm));
    }

    @Test
    public void testFormatInsertionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.CYSTEINE, 136, AminoAcidCode.ALANINE, 137).thenInsert(AminoAcidCode.GLYCINE, AminoAcidCode.METHIONINE).build();

        Assert.assertEquals("p.Cys136_Ala137insGlyMet", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDuplicationSimpleCode1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 76).thenDuplicate().build();

        Assert.assertEquals("p.C76dup", format.format(pm, AminoAcidCode.CodeType.ONE_LETTER));
    }

    @Test
    public void testFormatDuplicationSimpleCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 76).thenDuplicate().build();

        Assert.assertEquals("p.Cys76dup", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatDuplicationCode1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.CYSTEINE, 76, AminoAcidCode.GLUTAMIC_ACID, 79).thenDuplicate().build();

        Assert.assertEquals("p.C76_E79dup", format.format(pm, AminoAcidCode.CodeType.ONE_LETTER));
    }

    @Test
    public void testFormatDuplicationCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcidRange(AminoAcidCode.CYSTEINE, 76, AminoAcidCode.GLUTAMIC_ACID, 79).thenDuplicate().build();

        Assert.assertEquals("p.Cys76_Glu79dup", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatInitiationExtensionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-12, AminoAcidCode.VALINE).build();

        Assert.assertEquals("p.Met1Valext-12", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }

    @Test
    public void testFormatExtensionCode3() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.StartBuilding().selectAminoAcid(AminoAcidCode.STOP, 110)
                .thenTerminationExtension(17, AminoAcidCode.GLUTAMINE).build();

        Assert.assertEquals("p.Ter110Glnext*17", format.format(pm, AminoAcidCode.CodeType.THREE_LETTER));
    }
}