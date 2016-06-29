package com.nextprot.api.isoform.mapper.utils;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.commons.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SequencePositionMapper {

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
		
	static CodonNucleotideIndices getCodonNucleotidesIndicesInRanges(CodonNucleotidePositions codonPos, List<NucleotidePositionRange> positionsOfIsoformOnDNA) {

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


	static CodonNucleotidePositions getCodonNucleotidesPositionsInRanges(int isoformPos, List<NucleotidePositionRange> isoformPositionRangesOnDNA) {
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
	 * If aa is null or empty string we just check that position is < sequence length
	 *
	 * @param sequence the protein sequence
	 * @param pos position according to bio standard (first pos = 1)
	 * @param aas 1 or more amino acids (1 char / aa) (empty or null when it is an insertion)
	 * @return
	 */
	static boolean checkAminoAcidsFromPosition(String sequence, int pos, String aas) {

		boolean insertionMode = (aas == null) || aas.isEmpty();
		if (insertionMode) return checkSequencePosition(sequence, pos, true);
		return checkSequencePosition(sequence, pos, false) && sequence.startsWith(aas, pos-1);
	}

	/**
	 * Check that position exists in specified sequence at given mode.
	 *
	 * <h4>Insertion pos mode</h4>
	 * In insertion mode, insertion will be applied before amino-acid(s) at given position.
	 * <p>
	 *   They are 3 valid cases to consider (illustrated with sequence ABCDEF):
	 *
	 *   <ol>
	 *     <li>Before 1st AA: ABPCDEF (pos 1)</li>
	 *     <li>Internal: PABCDEF (pos 3)</li>
	 *     <li>After last AA: ABCDEFP (pos 7)</li>
	 *   </ol>
	 * </p>
	 *
	 * @param sequence the amino-acid sequence
	 * @param pos position according to bio standard (first pos = 1)
	 * @param insertionMode is true apply insertion rule else apply standard rule
     * @return true if position exists in the given sequence
     */
	static boolean checkSequencePosition(String sequence, int pos, boolean insertionMode) {
		Preconditions.checkNotNull(sequence);
		Preconditions.checkArgument(!sequence.isEmpty());
		Preconditions.checkArgument(pos>0, pos + ": invalid value (position should start at 1)");

		// An insertion at position p means
		if (insertionMode) return pos <= sequence.length()+1;
		return pos <= sequence.length();
	}

	public static List<NucleotidePositionRange> getPositionRangesFromEntries(List<Entry<Integer,Integer>> listEntries) {
		List<NucleotidePositionRange> result = new ArrayList<>();
		for (Entry<Integer,Integer> e: listEntries) result.add(new NucleotidePositionRange(e.getKey(), e.getValue()));
		return result;
	}
	
	public static List<NucleotidePositionRange> getPositionRangesFromPairs(List<Pair<Integer,Integer>> listPair) {
		List<NucleotidePositionRange> result = new ArrayList<>();
		for (Pair<Integer,Integer> e: listPair) result.add(new NucleotidePositionRange(e.getFirst(), e.getSecond()));
		return result;
	}
	
	
}
