package org.nextprot.api.commons.bio.variation.prot.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.impl.format.VariantHGVSFormat;

import java.util.Optional;

public class VariantSequenceOperatorTest {

    @Test
    public void testBuildSequenceVariantDuplication() throws Exception {

        assertOperation("KPLISMPEPTIDE", new VariantHGVSFormat().parse("p.Leu3_Met6dup"),
                6, 6, "M", "MLISM", "KPLISMLISMPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantSubstitution() throws Exception {

        assertOperation("KMLISMPEPTIDE", new VariantHGVSFormat().parse("p.Leu3Met"),
                3, 3, "L", "M", "KMMISMPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantInsertion() throws Exception {

        assertOperation("KMLISMRSPEPTIDE", new VariantHGVSFormat().parse("p.Arg7_Ser8insLeuIleSerMet"),
                7, 7, "R", "RLISM", "KMLISMRLISMSPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantDeletion() throws Exception {

        assertOperation("KMLISMRSPEPTIDE", new VariantHGVSFormat().parse("p.Leu3_Met6del"),
                3, 6, "LISM", "", "KMRSPEPTIDE");
    }

    @Test
    public void testBuildSequenceVariantDeletionInsertion() throws Exception {

        assertOperation("KMLIMLESMRSPEPTIDE", new VariantHGVSFormat().parse("p.Met5_Glu7delinsLeuIleSerMet"),
                5, 7, "MLE", "LISM", "KMLILISMSMRSPEPTIDE");
    }

    private static void assertOperation(String originalSequence, SequenceVariation sequenceVariation,
                                        int expectedPosStart, int expectedPosEnd, String expectedOriginal,
                                        String expectedVariant, String expectedVariantSequence) {

        Optional<VariantSequenceOperator> op = VariantSequenceOperator.findOperator(sequenceVariation.getSequenceChange());
        Assert.assertTrue(op.isPresent());

        Assert.assertEquals(expectedPosStart, op.get().selectBeginPositionInReferenceSequence(sequenceVariation.getVaryingSequence()));
        Assert.assertEquals(expectedPosEnd,  op.get().selectEndPositionInReferenceSequence(sequenceVariation.getVaryingSequence()));

        Assert.assertEquals(expectedOriginal, op.get().getAminoAcidTargetStringInReferenceSequence(originalSequence, sequenceVariation.getVaryingSequence()));
        Assert.assertEquals(expectedVariant, op.get().getAminoAcidReplacementString(originalSequence, sequenceVariation));
        Assert.assertEquals(expectedVariantSequence, op.get().buildVariantSequence(originalSequence, sequenceVariation));
    }
}