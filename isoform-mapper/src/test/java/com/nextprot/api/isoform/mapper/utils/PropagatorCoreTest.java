package com.nextprot.api.isoform.mapper.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.utils.NucleotidePositionRange;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class PropagatorCoreTest {

	
    @Test
    public void aminoAcidPosTest() {

    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 1, "A"));
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 3, "T"));
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 6, "I"));
    	// out of range => false
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 0, "T"));
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 7, "T"));
    	// wrong => false
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 1, "B"));
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 3, "B"));
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 6, "B"));

    	// special case with N aas
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 1, "AK"));
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 3, "TK"));
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 5, "LI"));
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 5, "LIT"));
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 6, "IY"));

    	// special case with 0 aa (null or length == 0)
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 1, null));
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 3, null));
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 6, null));
    	Assert.assertFalse(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 8, null));
    	
    	// case to discuss: interpreted as nothing just after the sequence = ok
    	Assert.assertTrue(PropagatorCore.checkAminoAcidAtPosition("AKTKLI", 7, null));
    }

    	
    @Test
    public void geneIsoformPosTest() {
    	
    	PropagatorCore.debug=true;
    	
    	// represent two coding regions of DNA mapped to an isoform sequence
    	List<NucleotidePositionRange> genePosRanges = new ArrayList<NucleotidePositionRange>();
    	genePosRanges.add(new NucleotidePositionRange(100,104));
    	genePosRanges.add(new NucleotidePositionRange(201,204));
    	
    	/*
    	 * 
    	 * nuNum      0   1   2   3   4            5   6   7   8
    	 * exons     |---- exon1 ------|          |-- exon 2 ---|
    	 * nuPos     100 101 102 103 104          201 202 203 204
    	 * codons    |--codon1--|-------codon2-------|--codon3--|
    	 * 
    	 * 
    	 */

    	CodonNucleotideIndices result;
    	CodonNucleotidePositions codonNuPos = new CodonNucleotidePositions();

    	// get isoform position of aa corresponding to codon with nucleotides at position 100,101,102
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(100);
    	codonNuPos.addNucleotidePosition(101);
    	codonNuPos.addNucleotidePosition(102);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(new Integer(1), result.getAminoAcidPosition());

    	// get isoform position of aa corresponding to codon with nucleotides at position 103 104 201
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	codonNuPos.addNucleotidePosition(201);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(new Integer(2), result.getAminoAcidPosition());

    	// get isoform position of aa corresponding to codon with nucleotides at position 202 203 204
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(202);
    	codonNuPos.addNucleotidePosition(203);
    	codonNuPos.addNucleotidePosition(204);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(new Integer(3), result.getAminoAcidPosition());

    	// trying to find a codon position having NON consecutive nucleotides i.e. at pos 201 203 204
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(201);
    	codonNuPos.addNucleotidePosition(203);
    	codonNuPos.addNucleotidePosition(204);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(false, result.areConsecutive());
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon position having nucleotide positions that are not in frame (3 nucl of same codon)
    	// i.e. at pos 101 102 103
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(101);
    	codonNuPos.addNucleotidePosition(102);
    	codonNuPos.addNucleotidePosition(103);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(false, result.areInFrame());
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon position having nucleotide positions that are not in frame (3 nucl of same codon)
    	// i.e. at pos 102 103 104
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(102);
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(false, result.areInFrame());
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos  77,78,79
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(77);
    	codonNuPos.addNucleotidePosition(78);
    	codonNuPos.addNucleotidePosition(79);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos 99,100,101
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(99);
    	codonNuPos.addNucleotidePosition(100);
    	codonNuPos.addNucleotidePosition(101);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
    	assertEquals(null, result.getAminoAcidPosition());

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos 103,104, 015
    	codonNuPos.clear();
    	codonNuPos.addNucleotidePosition(103);
    	codonNuPos.addNucleotidePosition(104);
    	codonNuPos.addNucleotidePosition(105);
    	result = PropagatorCore.getCodonNucleotidesIndicesInRanges(codonNuPos, genePosRanges);
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

    	CodonNucleotidePositions result;
    	
    	// get gene position of each nucl of the codon coding for aa of isoform at pos 1
    	result = PropagatorCore.getCodonNucleotidesPositionsInRanges(1, ranges);
    	assertEquals(new Integer(100), result.get(0));
    	assertEquals(new Integer(101), result.get(1));
    	assertEquals(new Integer(102), result.get(2));
    	assertEquals(true, result.isValid());

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 2
    	result = PropagatorCore.getCodonNucleotidesPositionsInRanges(2, ranges);
    	assertEquals(new Integer(103), result.get(0));
    	assertEquals(new Integer(104), result.get(1));
    	assertEquals(new Integer(201), result.get(2));
    	assertEquals(true, result.isValid());

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 3
    	result = PropagatorCore.getCodonNucleotidesPositionsInRanges(3, ranges);
    	assertEquals(new Integer(202), result.get(0));
    	assertEquals(new Integer(203), result.get(1));
    	assertEquals(new Integer(204), result.get(2));
    	assertEquals(true, result.isValid());
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 0 (out of range)
    	result = PropagatorCore.getCodonNucleotidesPositionsInRanges(0, ranges);
    	assertEquals(0, result.size());
    	assertEquals(false, result.isValid());
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 4 (out of range)
    	result = PropagatorCore.getCodonNucleotidesPositionsInRanges(4, ranges);
    	assertEquals(0, result.size());
    	assertEquals(false, result.isValid());
    }

	@Test
	public void testSequencePositions() {

		Assert.assertTrue(!PropagatorCore.checkSequencePosition("AKT", 0));
		Assert.assertTrue(PropagatorCore.checkSequencePosition("AKT", 1));
		Assert.assertTrue(PropagatorCore.checkSequencePosition("AKT", 2));
		Assert.assertTrue(PropagatorCore.checkSequencePosition("AKT", 3));
		Assert.assertTrue(!PropagatorCore.checkSequencePosition("AKT", 4));
	}

}
