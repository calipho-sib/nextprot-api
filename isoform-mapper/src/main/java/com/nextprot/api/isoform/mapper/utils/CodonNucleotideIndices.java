package com.nextprot.api.isoform.mapper.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the index on gene / master of the nucleotides of a codon
 * @author pmichel
 * 
 * nuNum      0   1   2   3   4            5   6   7   8  <== Codon nucleotide indices are here
 * exons     |---- exon1 ------|          |-- exon 2 ---|
 * nuPos     100 101 102 103 104          201 202 203 204  
 * codons    |--codon1--|-------codon2-------|--codon3--|
 * 
 */
public class CodonNucleotideIndices {

	public boolean debug=false;
	
	List<Integer> nuNum = new ArrayList<Integer>();
	
	public Integer get(int index) {
		return nuNum.get(index);
	}
	
	public void addNucleotideIndex(int index) {
		nuNum.add(new Integer(index));
	}
	
	public int size() {
		return nuNum.size();
	}
	
	public void clear() {
		nuNum.clear();
	}
	
	/**
	 * check that codon nucleotides indices are consecutive
	 * @return
	 */
	public boolean areConsecutive() {
		if (!has3Nucleotides()) return false;
		if (nuNum.get(0)+1 != nuNum.get(1) || nuNum.get(1)+1 != nuNum.get(2) ) {
			if (debug) System.out.println("nucleotides of codon are not consecutive");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * check that nucleotides indices are in same frame (optional check ?)
	 * @return
	 */
	public boolean areInFrame() {
		if (!has3Nucleotides()) return false;
		int isoPos = (nuNum.get(0) + 3) / 3;
		if ((nuNum.get(1) + 3) / 3 != isoPos || (nuNum.get(2) + 3) / 3 != isoPos) {
			if (debug) System.out.println("nucleotides not in frame");
			return false;
		} else {
			return true;
		}
	}

	public boolean has3Nucleotides() {
		if (debug && nuNum.size()!=3) System.out.println("codon has not 3 nucleotides");
		return nuNum.size()==3;
	}
	
	/**
	 * perform all checks
	 * @return
	 */
	public boolean isValid() {
		return has3Nucleotides() && areConsecutive() && areInFrame();
	}
	
	public Integer getAminoAcidPosition() {
		if (isValid()) {
			int aaPos = (nuNum.get(0) + 3) / 3;
			return new Integer(aaPos);
		} else {
			return null;
		}
	}
	
}
