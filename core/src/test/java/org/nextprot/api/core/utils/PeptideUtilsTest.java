package org.nextprot.api.core.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

public class PeptideUtilsTest extends CoreUnitBaseTest {

    @Test
    public void shouldFindThePeptideInsideTheSequenceWithTheModeIsoLeucineFalse()  {
    	String peptide = "PAAAFVNQHL";
    	String sequence = "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRRKRGIVEQCCTSICSLYQLENYCNMALW";
    	assertTrue(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequence, false));
    }

    @Test
    public void shouldNotFindModifiedPeptideInsideTheSequenceWithTheModeIsoLeucineFalse()  {
    	String peptide = "PAAAFVNQHI";
    	String sequence = "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRRKRGIVEQCCTSICSLYQLENYCNMALW";
    	assertFalse(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequence, false));
    }
    
    @Test
    public void shouldFindModifiedPeptideInsideTheSequenceWithTheModeIsoLeucineTrue()  {
    	String peptide = "PAAAFVNQHI";
    	String sequence = "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRRKRGIVEQCCTSICSLYQLENYCNMALW";
    	assertTrue(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequence, true));
    }

    @Test
    public void shouldNotFindCrazyPeptideInsideTheSequenceWithTheModeIsoLeucineTrue()  {
    	String peptide = "CRAZY";
    	String sequence = "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRRKRGIVEQCCTSICSLYQLENYCNMALW";
    	assertFalse(PeptideUtils.isPeptideContainedInTheSequence(peptide, sequence, true));
    }
    
}
