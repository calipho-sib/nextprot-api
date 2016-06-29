package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat;

import java.text.ParseException;

public class ProteinSequenceVariationHGVSParseMultiTest {

    ProteinSequenceVariationHGVSFormat format = new ProteinSequenceVariationHGVSFormat();

    @Ignore
    @Test
    public void testParseMultisVariants() throws ParseException {
        /*
MULTIS:
p.(=,Ile411_Gly426del)
         */
        format.parse("p.(=,Ile411_Gly426del)", AbstractProteinSequenceVariationFormat.ParsingMode.PERMISSIVE);
    }
}