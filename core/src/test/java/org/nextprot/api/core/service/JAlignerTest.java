package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.PamAligner;

import jaligner.Alignment;
import jaligner.NeedlemanWunsch;
import jaligner.NeedlemanWunschGotoh;
import jaligner.Sequence;
import jaligner.formats.Pair;
import jaligner.matrix.Matrix;
import jaligner.matrix.MatrixGenerator;
import jaligner.matrix.MatrixLoader;
import jaligner.util.SequenceParser;

//@ActiveProfiles({ "dev", "cache" })
public class JAlignerTest {
//extends CoreUnitBaseTest {

/*	
    @Autowired
    private GenomicMappingService genomicMappingService;
    @Autowired
    private IsoformService isoformService;
    @Autowired
*/
	
    //private MasterIdentifierService masterIdentifierService;

	
	@Test
	public void testPamAlignerPosConvert() {
		
		/*        12345678901   234567890123
		 * s1 : --ATTGCCCATGG---ATGCTGGATGAT
		 * mk :   ||||  |||||   |||||.||||| 
		 * s2 : GGATTG--CATGGTTTATGCTCGATGA-
		 *      123456  7890123456789012345
		*/
		
		String s1 = "ATTGCCCATGGATGCTGGATGAT";
		String s2 = "GGATTGCATGGTTTATGCTCGATGA";
		PamAligner pa = new PamAligner("s1", s1, "s2", s2);
		for (int i=0;i<26;i++) {
			PamAligner.SequenceJump sj = pa.getS2Pos(i);
			System.out.println(sj);
		}
		printAlignment(pa.getAlignment());
		
	}

		
    
	@Test
	public void testPam1() {
		
		/* 
		 * s1 : --ATTGCCCATGG---ATGCTGGATGAT
		 * mk :   ||||  |||||   |||||.||||| 
		 * s2 : GGATTG--CATGGTTTATGCTCGATGA-
		*/
		
		String s1 =   "ATTGCCCATGGATGCTGGATGAT"; // lng 23
		String s2 = "GGATTGCATGGTTTATGCTCGATGA";
		PamAligner pa = new PamAligner("s1", s1, "s2", s2);
		printAlignment(pa.getAlignment());
		System.out.println("s1 identities    : " + pa.getS1Identities());
		System.out.println("s1 ingaps cnt    : " + pa.getS1InnerGapCount());
		System.out.println("s2 ingaps cnt    : " + pa.getS2InnerGapCount());
		System.out.println("align ingaps cnt : " + pa.getInnerGapCount());
		Assert.assertTrue(0.82608694f == pa.getS1Identities());
		Assert.assertEquals(3,  pa.getS1InnerGapCount());
		Assert.assertEquals(2,  pa.getS2InnerGapCount());
		Assert.assertEquals(5,  pa.getInnerGapCount());
		//System.out.println( pa.getAlignment().getSummary());
	}

	@Test
	public void testCountInnerGapMethod() {
		char gapChar = '-';
		Assert.assertEquals(0, PamAligner.countInnerGap("".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("A".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("ACT".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("-".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("-A".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("-ACT".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("--".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("-A-".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("-ACT-".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("--A-".toCharArray(), gapChar));
		Assert.assertEquals(0, PamAligner.countInnerGap("--A--".toCharArray(), gapChar));
		Assert.assertEquals(1, PamAligner.countInnerGap("-AC-T-".toCharArray(), gapChar));
		Assert.assertEquals(2, PamAligner.countInnerGap("-A-C-T-".toCharArray(), gapChar));
		Assert.assertEquals(3, PamAligner.countInnerGap("-A--C-T-".toCharArray(), gapChar));
	}
	
	@Test
	public void testHasInnerGap() {
		char gapChar = '-';
		Assert.assertEquals(false, PamAligner.hasInnerGap("".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("A".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("ACT".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("-".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("-A".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("-ACT".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("--".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("-A-".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("-ACT-".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("--A-".toCharArray(), gapChar));
		Assert.assertEquals(false, PamAligner.hasInnerGap("--A--".toCharArray(), gapChar));
		Assert.assertEquals(true, PamAligner.hasInnerGap("-AC-T-".toCharArray(), gapChar));
		Assert.assertEquals(true, PamAligner.hasInnerGap("-A-C-T-".toCharArray(), gapChar));
	}
	
	private void printAlignment(Alignment al) {
		System.out.println("length           : " + al.getLength());
		System.out.println("identities       : " + al.getIdentity());
		System.out.println("similarities     : " + al.getSimilarity());
		System.out.println("start1           : " + al.getStart1());
		System.out.println("start2           : " + al.getStart2());
		System.out.println("gaps1            : " + al.getGaps1());
		System.out.println("gaps2            : " + al.getGaps2());
		System.out.println("score            : " + al.getScore());
		System.out.println("score no end gap : " + al.getScoreWithNoTerminalGaps());
		System.out.println("s1               : " + al.getName1() + new String(al.getSequence1()));
		System.out.println("mk               : " + new String(al.getMarkupLine()));
		System.out.println("s2               : " + al.getName2() + new String(al.getSequence2()));
	}

	
	 
	
}
