package org.nextprot.api.commons.bio.variation.seq;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.AminoAcidCode;

public class SequenceVariationTest {

    @Test
    public void testBuildSubstitution() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Arginine, 54).substitutedBy(AminoAcidCode.Cysteine).build();

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(54, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(54, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(!pm.isAminoAcidRange());

        Assert.assertTrue(pm.getSequenceChange() instanceof Substitution);
        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildAADeletion() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Lysine, 73).deletes().build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(73, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(73, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildRangeDeletion() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 487, AminoAcidCode.Leucine, 498).deletes().build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(487, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(498, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Deletion);
        Assert.assertNull(pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildFrameshift() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(AminoAcidCode.Alanine, 2).build();

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(682, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(682, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Frameshift);
        Assert.assertEquals(AminoAcidCode.Alanine, ((Frameshift.Change)pm.getSequenceChange().getValue()).getChangedAminoAcid());
        Assert.assertEquals(2, ((Frameshift.Change)pm.getSequenceChange().getValue()).getNewTerminationPosition());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBuildFrameshiftBadStopPos() throws Exception {

        new SequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Methionine, 682).thenFrameshift(AminoAcidCode.Alanine, 1).build();
    }

    @Test
    public void testBuildDeletion1AaAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Threonine, 399).deletedAndInserts(AminoAcidCode.Leucine).build();

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(399, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Threonine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(399, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[]{AminoAcidCode.Leucine}, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAasAndInsertion1() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Leucine, 330, AminoAcidCode.Alanine, 331).deletedAndInserts(AminoAcidCode.Phenylalanine).build();

        Assert.assertEquals(AminoAcidCode.Leucine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(330, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Alanine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(331, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Phenylalanine }, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDeletionMultiAndInsertionMulti() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.AsparticAcid, 419, AminoAcidCode.Arginine, 420).deletedAndInserts(AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine).build();

        Assert.assertEquals(AminoAcidCode.AsparticAcid, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(419, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Arginine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(420, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof DeletionAndInsertion);
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Serine, AminoAcidCode.Serine, AminoAcidCode.AsparticAcid, AminoAcidCode.Glycine }, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildInsertion() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Lysine, 2, AminoAcidCode.Methionine, 3).inserts(AminoAcidCode.Glutamine, AminoAcidCode.Serine, AminoAcidCode.Lysine).build();

        Assert.assertEquals(AminoAcidCode.Lysine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(2, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Methionine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(3, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Insertion);
        Assert.assertEquals(2, ((Insertion)pm.getSequenceChange()).getInsertAfterPos());
        Assert.assertArrayEquals(new AminoAcidCode[] { AminoAcidCode.Glutamine, AminoAcidCode.Serine, AminoAcidCode.Lysine }, (AminoAcidCode[]) pm.getSequenceChange().getValue());
    }

    @Test
    public void testBuildDuplication() throws Exception {

        /*
        p.Ala3_Ser5dup (several amino acids): a duplication of amino acids Ala3 to Ser5 in the sequence MetGlyAlaArgSerSerHis to MetGlyAlaArgSerAlaArgSerSerHis
         */

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcids(AminoAcidCode.Alanine, 3, AminoAcidCode.Serine, 5).duplicates().build();

        Assert.assertEquals(AminoAcidCode.Alanine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(3, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Serine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(5, pm.getLastChangingAminoAcidPos());

        Assert.assertTrue(pm.getSequenceChange() instanceof Duplication);
        Assert.assertEquals(5, ((Duplication)pm.getSequenceChange()).getInsertAfterPos());
    }

    @Test
    public void testBuildPtm() throws Exception {

        SequenceVariation pm = new SequenceVariation.FluentBuilder().aminoAcid(AminoAcidCode.Cysteine, 123).modifies(AminoAcidChange.S_Nitrosation).build();

        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getFirstChangingAminoAcid());
        Assert.assertEquals(123, pm.getFirstChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidCode.Cysteine, pm.getLastChangingAminoAcid());
        Assert.assertEquals(123, pm.getLastChangingAminoAcidPos());

        Assert.assertEquals(AminoAcidChange.S_Nitrosation, pm.getSequenceChange().getValue());
    }
}