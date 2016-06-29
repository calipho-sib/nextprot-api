package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;

import java.text.ParseException;

public class ProteinSequenceVariationHGVSParseDuplicationTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Ignore
    @Test
    public void testParseDuplicationsVariants() throws ParseException {
        /*
DUPLICATIONS:
p.Val417dup
         */
        format.parse("p.Val417dup", AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);
    }
}