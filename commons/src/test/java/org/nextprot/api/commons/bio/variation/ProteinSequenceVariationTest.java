package org.nextprot.api.commons.bio.variation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;

public class ProteinSequenceVariationTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(!pm.isAminoAcidRange());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildAADeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deleted().build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(73, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildRangeDeletion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deleted().build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildFrameshift() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(1).build();

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(682, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Frameshift);
        Assert.assertEquals(1, pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletion1AaAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(399, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.Leucine}, (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAasAndInsertion1() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Phenylalanine }, (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAndInsertionMulti() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine }, (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }

    @Test
    public void testBuildInsertion() throws Exception {

        ProteinSequenceVariation pm = new ProteinSequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 2, AminoAcidCode.Methionine, 3).inserts(AminoAcidCode.Glutamine, AminoAcidCode.Serine, AminoAcidCode.Lysine).build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(2, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(3, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getProteinSequenceChange() instanceof Insertion);
        Assert.assertEquals(2, ((Insertion)pm.getProteinSequenceChange()).getInsertAfterPos());
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Glutamine, AminoAcidCode.Serine, AminoAcidCode.Lysine }, (AminoAcidCode[]) pm.getProteinSequenceChange().getValue());
    }
}