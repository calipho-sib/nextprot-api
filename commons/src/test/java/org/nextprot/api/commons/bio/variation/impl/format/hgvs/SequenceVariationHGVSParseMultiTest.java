package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.bio.variation.SequenceVariationFormat;

import java.text.ParseException;

public class SequenceVariationHGVSParseMultiTest {

    SequenceVariationHGVSFormat format = new SequenceVariationHGVSFormat();

    @Ignore
    @Test
    public void testParseMultisVariants() throws ParseException {
        /*
MULTIS:
p.(=,Ile411_Gly426del)
         */
        format.parse("p.(=,Ile411_Gly426del)", SequenceVariationFormat.ParsingMode.PERMISSIVE);
    }
}