package org.nextprot.api.core.utils.seqmap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private final Log logger = LogFactory.getLog(CodonNucleotideIndices.class);

	private final List<Integer> nuNum = new ArrayList<>();
	
	public Integer get(int index) {
		return nuNum.get(index);
	}
	
	public void addNucleotideIndex(int index) {
		nuNum.add(index);
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
	boolean areConsecutive() {
		if (!has3Nucleotides()) return false;
		if (nuNum.get(0)+1 != nuNum.get(1) || nuNum.get(1)+1 != nuNum.get(2) ) {
            logger.warn("nucleotides of codon are not consecutive");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * check that nucleotides indices are in same frame (optional check ?)
	 * @return
	 */
	boolean areInFrame() {
		if (!has3Nucleotides()) return false;
		int isoPos = (nuNum.get(0) + 3) / 3;
		if ((nuNum.get(1) + 3) / 3 != isoPos || (nuNum.get(2) + 3) / 3 != isoPos) {
			logger.debug("nucleotides not in frame");
			return false;
		} else {
			return true;
		}
	}

	boolean has3Nucleotides() {
		if (nuNum.size()!=3) logger.debug("codon has not 3 nucleotides");
		return nuNum.size()==3;
	}

    /**
	 * perform all checks
	 * @return
	 */
	public boolean isValid() {
		return areConsecutive() && areInFrame();
	}
	
	public Integer getAminoAcidPosition() {
		if (isValid()) {
			return (nuNum.get(0) + 3) / 3;
		} else {
			return null;
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (nuNum.size()>0) sb.append("|nu0:"+ nuNum.get(0) + "|"); else sb.append("|-|");
		if (nuNum.size()>1) sb.append("nu1:"+ nuNum.get(1) + "|"); else sb.append("-|");
		if (nuNum.size()>2) sb.append("nu2:"+ nuNum.get(2) + "|"); else sb.append("-|");
		sb.append(" - aaPosition:" + getAminoAcidPosition());
		sb.append(" - nuConseccutive:" + areConsecutive());
		sb.append(" - nuInFrame:" + areInFrame());
		sb.append(" - valid:"+isValid());
		return sb.toString();
	}
	
}
