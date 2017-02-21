package org.nextprot.api.commons.bio.variation.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

public class SequenceVariationImplTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.ARGININE, 54).thenSubstituteWith(AminoAcidCode.CYSTEINE).build();

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(!pm.isMultipleChangingAminoAcids());

        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildAADeletion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.LYSINE, 73).thenDelete().build();

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(73, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildRangeDeletion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcidRange(AminoAcidCode.LYSINE, 487, AminoAcidCode.LEUCINE, 498).thenDelete().build();

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());

        Assert.assertEquals(SequenceChange.Type.DELETION, pm.getSequenceChange().getType());
        Assert.assertNull(pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildFrameshift() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 682).thenFrameshift(AminoAcidCode.ALANINE, 2).build();

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(682, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Frameshift);
        Assert.assertEquals(AminoAcidCode.ALANINE, ((Frameshift.Change)pm.getSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildFrameshiftBadStopPos() throws Exception {

        new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 682).thenFrameshift(AminoAcidCode.ALANINE, 1).build();
    }

    @Test
    public void testBuildDeletion1AaAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.THREONINE, 399).thenDeleteAndInsert(AminoAcidCode.LEUCINE).build();

        Assert.assertEquals(AminoAcidCode.THREONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.THREONINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(399, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.LEUCINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAasAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcidRange(AminoAcidCode.LEUCINE, 330, AminoAcidCode.ALANINE, 331).thenDeleteAndInsert(AminoAcidCode.PHENYLALANINE).build();

        Assert.assertEquals(AminoAcidCode.LEUCINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.ALANINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.PHENYLALANINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAndInsertionMulti() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcidRange(AminoAcidCode.ASPARTIC_ACID, 419, AminoAcidCode.ARGININE, 420).thenDeleteAndInsert(AminoAcidCode.SERINE, AminoAcidCode.SERINE, AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.GLYCINE).build();

        Assert.assertEquals(AminoAcidCode.ASPARTIC_ACID, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.ARGININE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.SERINE, AminoAcidCode.SERINE, AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.GLYCINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildInsertion() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcidRange(AminoAcidCode.LYSINE, 2, AminoAcidCode.METHIONINE, 3).thenInsert(AminoAcidCode.GLUTAMINE, AminoAcidCode.SERINE, AminoAcidCode.LYSINE).build();

        Assert.assertEquals(AminoAcidCode.LYSINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(2, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(3, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Insertion);
        Assert.assertEquals(2, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.GLUTAMINE, AminoAcidCode.SERINE, AminoAcidCode.LYSINE}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDuplication() throws Exception {

        /*
        p.Ala3_Ser5dup (several amino acids): a duplication of amino acids Ala3 to Ser5 in the sequence MetGlyAlaArgSerSerHis to MetGlyAlaArgSerAlaArgSerSerHis
         */

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcidRange(AminoAcidCode.ALANINE, 3, AminoAcidCode.SERINE, 5).thenDuplicate().build();

        Assert.assertEquals(AminoAcidCode.ALANINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(3, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.SERINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(5, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Duplication);
        Assert.assertEquals(5, ((Duplication)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testBuildPtm() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.CYSTEINE, 123).thenAddModification(AminoAcidModification.S_NITROSATION).build();

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(123, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.CYSTEINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(123, pm.getLastChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidModification.S_NITROSATION, pm.getSequenceChange().getValue());
    }

    // p.Met1ext-5
    @Test
    public void testBuildExtensionInit1() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-5, AminoAcidCode.METHIONINE).build();

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(1, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof ExtensionInitiation);
        Assert.assertEquals(-5, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.METHIONINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    // p.Met1Valext-12
    @Test
    public void testBuildExtensionInit2() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.METHIONINE, 1)
                .thenInitiationExtension(-12, AminoAcidCode.VALINE).build();

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(1, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.METHIONINE, pm.getLastChangingAminoAcid());
        Assert.assertEquals(1, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof ExtensionInitiation);
        Assert.assertEquals(-12, ((ExtensionInitiation)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.VALINE, ((ExtensionInitiation)pm.getSequenceChange()).getValue());
    }

    // p.Ter110GlnextTer17
    @Test
    public void testBuildExtensionTerm() throws Exception {

        SequenceVariation pm = new SequenceVariationImpl.FluentBuilding().selectAminoAcid(AminoAcidCode.STOP, 110)
                .thenTerminationExtension(17, AminoAcidCode.GLUTAMINE).build();

        Assert.assertEquals(AminoAcidCode.STOP, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(110, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.STOP, pm.getLastChangingAminoAcid());
        Assert.assertEquals(110, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof ExtensionTermination);
        Assert.assertEquals(17, ((ExtensionTermination)pm.getSequenceChange()).getNewPos());
        Assert.assertEquals(AminoAcidCode.GLUTAMINE, ((ExtensionTermination)pm.getSequenceChange()).getValue());
    }
}