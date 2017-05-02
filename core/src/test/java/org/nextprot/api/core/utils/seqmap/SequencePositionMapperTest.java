package org.nextprot.api.core.utils.seqmap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.utils.NucleotidePositionRange;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class SequencePositionMapperTest {

	static
	{
	    Logger rootLogger = Logger.getRootLogger();
	    rootLogger.setLevel(Level.DEBUG);
	    rootLogger.addAppender(new ConsoleAppender(
	               new PatternLayout("\n%-6r [%p] %c - %m%n")));
	}
	
	private final static Log logger = LogFactory.getLog(SequencePositionMapperTest.class);

	@Test
	public void testCheckSequencePositions() {

		Assert.assertTrue(SequencePositionMapper.checkSequencePosition("AKT", 1, false));
		Assert.assertTrue(SequencePositionMapper.checkSequencePosition("AKT", 2, false));
		Assert.assertTrue(SequencePositionMapper.checkSequencePosition("AKT", 3, false));
		Assert.assertFalse(SequencePositionMapper.checkSequencePosition("AKT", 4, false));
	}

	@Test
	public void testCheckSequencePositionInsertion() {

		Assert.assertTrue(SequencePositionMapper.checkSequencePosition("AKT", 4, true));
	}

	@Test(expected = IllegalArgumentException.class)
	public void firstPositionShouldNotBeZero() {

		SequencePositionMapper.checkSequencePosition("AKTKLI", 0, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenNegativePosition() {

		SequencePositionMapper.checkSequencePosition("AKTKLI", -1, false);
	}

    @Test
    public void aminoAcidPosTest() {

    	Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 1, "A"));
    	Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 3, "T"));
    	Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 6, "I"));
    	Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 7, "T"));
    	// wrong => false
    	Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 1, "B"));
    	Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 3, "B"));
    	Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 6, "B"));

    	// special case with N aas
    	Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 1, "AK"));
    	Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 3, "TK"));
    	Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 5, "LI"));
    	Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 5, "LIT"));
    	Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 6, "IY"));
    }

	@Test
	public void testCheckInsertionPos() {

		// special case with 0 aa (null or length == 0) that is insertion
		Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 1, null));
		Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 3, null));
		Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 6, null));

		// insertion just after the last amino-acid is valid
		Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 7, null));
		// insertion n position after the last amino-acid is not valid
		Assert.assertFalse(SequencePositionMapper.checkAminoAcidsFromPosition("AKTKLI", 8, null));
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNPEWhenSequenceIsNull() {

		SequencePositionMapper.checkAminoAcidsFromPosition(null, 1, "A");
	}

    @Test
    public void geneIsoformPosTestNotInFrameCrossExon() {
    	// represent two coding regions of DNA mapped to an isoform sequence
    	List<NucleotidePositionRange> genePosRanges = new ArrayList<NucleotidePositionRange>();
    	genePosRanges.add(new NucleotidePositionRange(100,104));
    	genePosRanges.add(new NucleotidePositionRange(201,204));    	
    	/*
    	 * nuNum      0   1   2   3   4            5   6   7   8
    	 * exons     |---- exon1 ------|          |-- exon 2 ---|
    	 * nuPos     100 101 102 103 104          201 202 203 204
    	 * codons    |--codon1--|-------codon2-------|--codon3--|
    	 */
    	CodonNucleotideIndices result;
    	GeneMasterCodonPosition codonNuPos = new GeneMasterCodonPosition();
    	// get isoform position of aa corresponding to codon with nucleotides at position 103 104 201
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	codonNuPos.addNucleotidePosition(201);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	logger.debug(result);
    	assertEquals(true, result.areInFrame());
    	assertEquals(new Integer(2), result.getAminoAcidPosition());
    }

    
    @Test
    public void geneIsoformPosTest() {
    	
    	// represent two coding regions of DNA mapped to an isoform sequence
    	List<NucleotidePositionRange> genePosRanges = new ArrayList<NucleotidePositionRange>();
    	genePosRanges.add(new NucleotidePositionRange(100,104));
    	genePosRanges.add(new NucleotidePositionRange(201,204));
    	/*
    	 * nuNum      0   1   2   3   4            5   6   7   8
    	 * exons     |---- exon1 ------|          |-- exon 2 ---|
    	 * nuPos     100 101 102 103 104          201 202 203 204
    	 * codons    |--codon1--|-------codon2-------|--codon3--|
    	 */
    	CodonNucleotideIndices result;
    	GeneMasterCodonPosition codonNuPos = new GeneMasterCodonPosition();

    	// get isoform pos of aa corresponding to codon with nu at pos 202 203 204
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(202);
    	codonNuPos.addNucleotidePosition(203);
    	codonNuPos.addNucleotidePosition(204);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(true,result.has3Nucleotides());
    	assertEquals(new Integer(3), result.getAminoAcidPosition());
    	
    	
    	// get isoform position of aa corresponding to codon with nucleotides at position 100,101,102
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(100);
    	codonNuPos.addNucleotidePosition(101);
    	codonNuPos.addNucleotidePosition(102);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(new Integer(1), result.getAminoAcidPosition());

    	// get isoform position of aa corresponding to codon with nucleotides at position 103 104 201
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	codonNuPos.addNucleotidePosition(201);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(new Integer(2), result.getAminoAcidPosition());

    	// get isoform position of aa corresponding to codon with nucleotides at position 202 203 204
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(202);
    	codonNuPos.addNucleotidePosition(203);
    	codonNuPos.addNucleotidePosition(204);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(new Integer(3), result.getAminoAcidPosition());

    	// trying to find a codon position having NON consecutive nucleotides i.e. at pos 201 203 204
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(201);
    	codonNuPos.addNucleotidePosition(203);
    	codonNuPos.addNucleotidePosition(204);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(false, result.areConsecutive());
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon position having nucleotide positions that are not in frame (3 nucl of same codon)
    	// i.e. at pos 101 102 103
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(101);
    	codonNuPos.addNucleotidePosition(102);
    	codonNuPos.addNucleotidePosition(103);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(false, result.areInFrame());
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon position having nucleotide positions that are not in frame (3 nucl of same codon)
    	// i.e. at pos 102 103 104
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(102);
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(false, result.areInFrame());
    	assertEquals(null, result.getAminoAcidPosition());
    	
    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos  77,78,79
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(77);
    	codonNuPos.addNucleotidePosition(78);
    	codonNuPos.addNucleotidePosition(79);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos 99,100,101
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(99);
    	codonNuPos.addNucleotidePosition(100);
    	codonNuPos.addNucleotidePosition(101);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos 103,104, 015
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	codonNuPos.addNucleotidePosition(105);
    	result = SequencePositionMapper.getCodonNucleotideIndices(codonNuPos, genePosRanges);
    	assertEquals(null, result.getAminoAcidPosition());
    	
    }
	
    @Test
    public void geneCodonPosTest() {
 
       	// represent two coding regions of DNA (gene,master) mapped to an isoform sequence
    	List<NucleotidePositionRange> ranges = new ArrayList<NucleotidePositionRange>();
    	ranges.add(new NucleotidePositionRange(100,104));
    	ranges.add(new NucleotidePositionRange(201,204));
    	/*
    	 * 
    	 * nuNum      0   1   2   3   4            5   6   7   8
    	 * exons     |---- exon1 ------|          |-- exon 2 ---|
    	 * nuPos     100 101 102 103 104          201 202 203 204
    	 * codons    |--codon1--|-------codon2-------|--codon3--|
    	 * 
    	 * 
    	 */

    	GeneMasterCodonPosition result;
    	
    	// get gene position of each nucl of the codon coding for aa of isoform at pos 1
    	result = SequencePositionMapper.getCodonPositionOnMaster(1, ranges);
    	assertEquals(new Integer(100), result.getNucleotidePosition(0));
    	assertEquals(new Integer(101), result.getNucleotidePosition(1));
    	assertEquals(new Integer(102), result.getNucleotidePosition(2));
    	assertEquals(true, result.isValid());

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 2
    	result = SequencePositionMapper.getCodonPositionOnMaster(2, ranges);
    	assertEquals(new Integer(103), result.getNucleotidePosition(0));
    	assertEquals(new Integer(104), result.getNucleotidePosition(1));
    	assertEquals(new Integer(201), result.getNucleotidePosition(2));
    	assertEquals(true, result.isValid());

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 3
    	result = SequencePositionMapper.getCodonPositionOnMaster(3, ranges);
    	assertEquals(new Integer(202), result.getNucleotidePosition(0));
    	assertEquals(new Integer(203), result.getNucleotidePosition(1));
    	assertEquals(new Integer(204), result.getNucleotidePosition(2));
    	assertEquals(true, result.isValid());
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 0 (out of range)
    	result = SequencePositionMapper.getCodonPositionOnMaster(0, ranges);
    	assertEquals(0, result.size());
    	assertEquals(false, result.isValid());
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 4 (out of range)
    	result = SequencePositionMapper.getCodonPositionOnMaster(4, ranges);
    	assertEquals(0, result.size());
    	assertEquals(false, result.isValid());
    }

	@Test
	public void checkAminoAcidsFromPositionWhenMultipleD() {

		String sequence = "MDDRCYPVIFPDERNFRPFTSDS";
		Assert.assertTrue(SequencePositionMapper.checkAminoAcidsFromPosition(sequence, 3, "D"));
	}
}
