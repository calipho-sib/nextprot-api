package org.nextprot.api.core.utils.seqmap;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.commons.utils.NucleotidePositionRange;
import org.nextprot.api.commons.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class SequencePositionMapper {

	private final static Log logger = LogFactory.getLog(SequencePositionMapper.class);

	/*
	 * 
	 * nuNum      0   1   2   3   4            5   6   7   8
	 * exons     |---- exon1 ------|          |-- exon 2 ---|
	 * nuPos     100 101 102 103 104          201 202 203 204
	 * codons    |--codon1--|-------codon2-------|--codon3--|
	 * 
	 * 
	 */

	static CodonNucleotideIndices getCodonNucleotideIndices(GeneMasterCodonPosition codonPos, List<NucleotidePositionRange> positionsOfIsoformOnDNA) {

		logger.debug("----------------------------------------------------------");
		int lowNum = 0;
		CodonNucleotideIndices codonPosInTranscript = new CodonNucleotideIndices();
		for (NucleotidePositionRange range: positionsOfIsoformOnDNA) {
			int nu1Pos = range.getLower();
			int nu2Pos = range.getUpper();
			int highNum = lowNum + nu2Pos - nu1Pos ;
			logger.debug("nu1Pos:"+ nu1Pos + " nu2Pos:" + nu2Pos + " lowNum:" + lowNum + " highNum:" + highNum);
			while (true) {
				int nuIndex = codonPosInTranscript.size();
				int nuPos = codonPos.getNucleotidePosition(nuIndex);
				logger.debug("nuPos("+nuIndex+")=" + nuPos);
				if (nuPos < nu1Pos || nuPos > nu2Pos) break;
				int nuNum = lowNum + nuPos - nu1Pos;
				logger.debug("adding codon nucelotide number:" + nuNum);
				codonPosInTranscript.addNucleotideIndex(nuNum);
				if (codonPosInTranscript.size()==3) {
					return codonPosInTranscript;
				}
			}
			lowNum=highNum + 1;
		}
		logger.debug("codon not found in the gene mapping ranges, " + codonPosInTranscript.size() + " nucleotides found");
		return codonPosInTranscript;
	}

	/**
	 * Get the codon position on gene master that corresponds to the given amino-acid position on isoform
	 *
	 * @param isoformPos the aa position on isoform
	 * @param isoformMasterMapping the list of isoform to gene master mapping
     * @return the codon position on gene master
     */
	static GeneMasterCodonPosition getCodonPositionOnMaster(int isoformPos, List<NucleotidePositionRange> isoformMasterMapping) {
		int firstNucPos = isoformPos * 3 - 3;
		//if (debug) System.out.println("nu1Num:" + nu1Num);
		int lowNum = 0;
		GeneMasterCodonPosition result = new GeneMasterCodonPosition();
		for (NucleotidePositionRange range: isoformMasterMapping) {
			int firstRangeNucPos = range.getLower();
			int lastRangeNucPos = range.getUpper();
			int highNum = lowNum + lastRangeNucPos - firstRangeNucPos ;
			//if (debug) System.out.println("nu1Pos:"+ nu1Pos + " nu2Pos:" + nu2Pos + " lowNum:" + lowNum + " highNum:" + highNum);
			while (true) {
				int nuIndex = result.size();
				int nuNum = firstNucPos + nuIndex;
				//if (debug) System.out.println("nuNum:" + nuNum);
				if (nuNum < lowNum || nuNum > highNum) break;
				result.addNucleotidePosition(firstRangeNucPos + nuNum-lowNum);
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
	 * @return true if aas are found at pos in sequence
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
