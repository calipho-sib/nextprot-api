package org.nextprot.api.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PropagatorTest {

    @Test
    public void geneIsoformPosTest() {
 
    	
    	// represent two coding regions of gene / master mapped to an isoform sequence
    	Map<Integer,Integer> map = new TreeMap<Integer,Integer>();
    	map.put(100, 104);
    	map.put(201, 204);
    	List<Map.Entry<Integer,Integer>> genePosRanges = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());

    	/*
    	 * 
    	 * nuNum      0   1   2   3   4            5   6   7   8
    	 * exons     |---- exon1 ------|          |-- exon 2 ---|
    	 * nuPos     100 101 102 103 104          201 202 203 204
    	 * codons    |--codon1--|-------codon2-------|--codon3--|
    	 * 
    	 * 
    	 */

    	Integer result;
    	List<Integer> codonNuPos = new ArrayList<Integer>();

    	// get isoform position of aa corresponding to codon with nucleotides at position 100,101,102
    	codonNuPos.clear();
    	codonNuPos.add(100);
    	codonNuPos.add(101);
    	codonNuPos.add(102);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(new Integer(1), result);

    	// get isoform position of aa corresponding to codon with nucleotides at position 103 104 201
    	codonNuPos.clear();
    	codonNuPos.add(103);
    	codonNuPos.add(104);
    	codonNuPos.add(201);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(new Integer(2), result);

    	// get isoform position of aa corresponding to codon with nucleotides at position 202 203 204
    	codonNuPos.clear();
    	codonNuPos.add(202);
    	codonNuPos.add(203);
    	codonNuPos.add(204);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(new Integer(3), result);

    	// trying to find a codon position having NON consecutive nucleotides i.e. at pos 201 203 204
    	codonNuPos.clear();
    	codonNuPos.add(201);
    	codonNuPos.add(203);
    	codonNuPos.add(204);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(null, result);

    	// trying to find a codon position having nucleotide positions that are not in frame (3 nucl of same codon)
    	// i.e. at pos 101 102 103
    	codonNuPos.clear();
    	codonNuPos.add(101);
    	codonNuPos.add(102);
    	codonNuPos.add(103);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(null, result);

    	// trying to find a codon position having nucleotide positions that are not in frame (3 nucl of same codon)
    	// i.e. at pos 102 103 104
    	codonNuPos.clear();
    	codonNuPos.add(102);
    	codonNuPos.add(103);
    	codonNuPos.add(104);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(null, result);

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos  77,78,79
    	codonNuPos.clear();
    	codonNuPos.add(77);
    	codonNuPos.add(78);
    	codonNuPos.add(79);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(null, result);

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos 99,100,101
    	codonNuPos.clear();
    	codonNuPos.add(99);
    	codonNuPos.add(100);
    	codonNuPos.add(101);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(null, result);

    	// trying to find a codon having nucleotide positions out of the gene mapping ranges
    	// i.e. at pos 103,104, 015
    	codonNuPos.clear();
    	codonNuPos.add(103);
    	codonNuPos.add(104);
    	codonNuPos.add(105);
    	result = Propagator.getIsoformPosition(codonNuPos, genePosRanges);
    	assertEquals(null, result);

    	
    	
    }
	
    @Test
    public void geneCodonPosTest() {
 
    	
    	// represent two coding regions of gene / master mapped to an isoform sequence
    	Map<Integer,Integer> map = new TreeMap<Integer,Integer>();
    	map.put(100, 104);
    	map.put(201, 204);
    	List<Map.Entry<Integer,Integer>> list = new ArrayList<Map.Entry<Integer,Integer>>(map.entrySet());

    	/*
    	 * 
    	 * nuNum      0   1   2   3   4            5   6   7   8
    	 * exons     |---- exon1 ------|          |-- exon 2 ---|
    	 * nuPos     100 101 102 103 104          201 202 203 204
    	 * codons    |--codon1--|-------codon2-------|--codon3--|
    	 * 
    	 * 
    	 */

    	List<Integer> result;
    	
    	// get gene position of each nucl of the codon coding for aa of isoform at pos 1
    	result = Propagator.getGeneCodonPositions(1, list);
    	assertEquals(new Integer(100), result.get(0));
    	assertEquals(new Integer(101), result.get(1));
    	assertEquals(new Integer(102), result.get(2));

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 2
    	result = Propagator.getGeneCodonPositions(2, list);
    	assertEquals(new Integer(103), result.get(0));
    	assertEquals(new Integer(104), result.get(1));
    	assertEquals(new Integer(201), result.get(2));

    	// get gene position of each nucl of the codon coding for aa of isoform at pos 3
    	result = Propagator.getGeneCodonPositions(3, list);
    	assertEquals(new Integer(202), result.get(0));
    	assertEquals(new Integer(203), result.get(1));
    	assertEquals(new Integer(204), result.get(2));
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 0 (out of range)
    	result = Propagator.getGeneCodonPositions(0, list);
    	assertEquals(0, result.size());
    	
       	// get gene position of each nucl of the codon coding for aa of isoform at pos 4 (out of range)
    	result = Propagator.getGeneCodonPositions(4, list);
    	assertEquals(0, result.size());
    	
    }
	
}
