package org.nextprot.api.core.utils.seqmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the positions on gene / master of the nucleotides of a codon
 * @author pmichel
 * 
 * nuNum      0   1   2   3   4            5   6   7   8
 * exons     |---- exon1 ------|          |-- exon 2 ---|
 * nuPos     100 101 102 103 104          201 202 203 204  <== Codon nucleotide positions are here
 * codons    |--codon1--|-------codon2-------|--codon3--|
 * 
 * 
 *
 */
public class GeneMasterCodonPosition {

	private final List<Integer> nuPos = new ArrayList<>();
	
	public Integer getNucleotidePosition(int index) {
		return nuPos.get(index);
	}
	
	public void addNucleotidePosition(int position) {
		nuPos.add(position);
	}
	
	public int size() {
		return nuPos.size();
	}
	
	public void clear() {
		nuPos.clear();
	}
	
	public boolean isValid() {
		boolean status = true;
		if (nuPos.size()!=3) status=false;
		return status;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (nuPos.size()>0) sb.append("|pos0:" + nuPos.get(0) + "|"); else sb.append("|-|");
		if (nuPos.size()>1) sb.append("pos1:" + nuPos.get(1) + "|"); else sb.append("-|");
		if (nuPos.size()>2) sb.append("pos2:" + nuPos.get(2) + "|"); else sb.append("-|");
		sb.append(" - valid: " + isValid());
		return sb.toString();
	}
	
}
