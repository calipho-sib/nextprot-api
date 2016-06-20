package org.nextprot.api.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class PropagatorCore {

	/*
	 * 
	 * nuNum      0   1   2   3   4            5   6   7   8
	 * exons     |---- exon1 ------|          |-- exon 2 ---|
	 * nuPos     100 101 102 103 104          201 202 203 204
	 * codons    |--codon1--|-------codon2-------|--codon3--|
	 * 
	 * 
	 */

	public static boolean debug = false;
		
	public static CodonNucleotideIndices getCodonNucleotidesIndicesInRanges(CodonNucleotidePositions codonPos, List<NucleotidePositionRange> positionsOfIsoformOnDNA) {
		
		if (debug) System.out.println("----------------------------------------------------------");
		int lowNum = 0;
		CodonNucleotideIndices codonNum = new CodonNucleotideIndices();
		codonNum.debug=true;
		for (NucleotidePositionRange range: positionsOfIsoformOnDNA) {
			int nu1Pos = range.getLower();
			int nu2Pos = range.getUpper();
			int highNum = lowNum + nu2Pos - nu1Pos ;
			if (debug) System.out.println("nu1Pos:"+ nu1Pos + " nu2Pos:" + nu2Pos + " lowNum:" + lowNum + " highNum:" + highNum);
			while (true) {
				int nuIndex = codonNum.size();
				int nuPos = codonPos.get(nuIndex);
				if (debug) System.out.println("nuPos("+nuIndex+")=" + nuPos);
				if (nuPos < nu1Pos || nuPos > nu2Pos) break;
				int nuNum = lowNum + nuPos - nu1Pos;
				if (debug) System.out.println("adding codon nucelotide number:" + nuNum);
				codonNum.addNucleotideIndex(nuNum);
				if (codonNum.size()==3) {
					return codonNum;
				}
			}
			lowNum=highNum + 1;
		}
		if (debug) System.out.println("codon not found in the gene mapping ranges, " + codonNum.size() + " nucleotides found");
		return codonNum;		
	}	
	
	
	public static CodonNucleotidePositions getCodonNucleotidesPositionsInRanges(int isoformPos, List<NucleotidePositionRange> isoformPositionRangesOnDNA) {
		int nu1Num = isoformPos * 3 - 3;  
		//if (debug) System.out.println("nu1Num:" + nu1Num);
		int lowNum = 0;
		CodonNucleotidePositions result = new CodonNucleotidePositions();
		for (NucleotidePositionRange range: isoformPositionRangesOnDNA) {
			int nu1Pos = range.getLower();
			int nu2Pos = range.getUpper();
			int highNum = lowNum + nu2Pos - nu1Pos ;
			//if (debug) System.out.println("nu1Pos:"+ nu1Pos + " nu2Pos:" + nu2Pos + " lowNum:" + lowNum + " highNum:" + highNum);
			while (true) {
				int nuIndex = result.size();
				int nuNum = nu1Num + nuIndex;
				//if (debug) System.out.println("nuNum:" + nuNum);
				if (nuNum < lowNum || nuNum > highNum) break;
				result.addNucleotidePosition(nu1Pos + nuNum-lowNum);
				if (result.size()==3) return result; 
			}
			lowNum=highNum + 1;
		}
		return result;		
	}	
	
	/**
	 * Check that we have amino acid aa(s) in isoform sequence at position pos.
	 * If aa iss null or empty string we just check that position is < sequence lenght
	 * @param isoform
	 * @param pos position according to bio standard (first pos = 1)
	 * @param aa 0, 1 or more amino acids (1 char / aa)
	 * @return
	 */
	public static boolean checkAminoAcidPosition(String sequence, int pos, String aa) {
		int strPos = pos-1; // converts bio std to geek std
		int strLng = aa==null ? 0 : aa.length();
		if (strPos + strLng > sequence.length()) return false;
		if (aa==null || aa.length()==0) return true;
		return sequence.indexOf(aa) == strPos;
	}	
	
	public static List<NucleotidePositionRange> getPositionRangesFromEntries(List<Entry<Integer,Integer>> listEntries) {
		List<NucleotidePositionRange> result = new ArrayList<NucleotidePositionRange>();
		for (Entry<Integer,Integer> e: listEntries) result.add(new NucleotidePositionRange(e.getKey(), e.getValue()));
		return result;
	}
	
	public static List<NucleotidePositionRange> getPositionRangesFromPairs(List<Pair<Integer,Integer>> listPair) {
		List<NucleotidePositionRange> result = new ArrayList<NucleotidePositionRange>();
		for (Pair<Integer,Integer> e: listPair) result.add(new NucleotidePositionRange(e.getFirst(), e.getSecond()));
		return result;
	}
	
	
}
