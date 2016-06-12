package org.nextprot.api.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Propagator {

	/*
	 * 
	 * nuNum      0   1   2   3   4            5   6   7   8
	 * exons     |---- exon1 ------|          |-- exon 2 ---|
	 * nuPos     100 101 102 103 104          201 202 203 204
	 * codons    |--codon1--|-------codon2-------|--codon3--|
	 * 
	 * 
	 */


	public static Integer getIsoformPosition(List<Integer> codonPos, List<Entry<Integer,Integer>> positionsOfIsoformOnReferencedGene) {
		
		System.out.println("----------------------------------------------------------");
		int lowNum = 0;
		List<Integer> codonNum = new ArrayList<Integer>();
		for (Entry<Integer,Integer> e: positionsOfIsoformOnReferencedGene) {
			int nu1Pos = e.getKey();
			int nu2Pos = e.getValue();
			int highNum = lowNum + nu2Pos - nu1Pos ;
			System.out.println("nu1Pos:"+ nu1Pos + " nu2Pos:" + nu2Pos + " lowNum:" + lowNum + " highNum:" + highNum);
			while (true) {
				int nuIndex = codonNum.size();
				int nuPos = codonPos.get(nuIndex);
				System.out.println("nuPos("+nuIndex+")=" + nuPos);
				if (nuPos < nu1Pos || nuPos > nu2Pos) break;
				int nuNum = lowNum + nuPos - nu1Pos;
				System.out.println("adding codon nucelotide number:" + nuNum);
				codonNum.add(nuNum);
				if (codonNum.size()==3) {
					// check that codon nucleotides nums are consecutive
					for (int i=0;i<3;i++) System.out.println("nu"+i+ " at gene pos " + codonPos.get(i) + " is nucleotide num " + codonNum.get(i));
					if (codonNum.get(0)+1 != codonNum.get(1) || codonNum.get(1)+1 != codonNum.get(2) ) {
						System.out.println("nucleotides of codon are not consecutive");
						return null;
					}
					// check that nucleotides num are in same frame (optional check ?)
					int isoPos = (codonNum.get(0) + 3) / 3;
					if ((codonNum.get(1) + 3) / 3 != isoPos || (codonNum.get(2) + 3) / 3 != isoPos) {
						System.out.println("nucleotides not in frame");
						return null;
					}
					System.out.println("returning iso pos ( nuNum:" + codonNum.get(0) + " ) = " + isoPos);
					return new Integer(isoPos); 
				}
			}
			lowNum=highNum + 1;
		}
		System.out.println("codon not found in the gene mapping ranges, " + codonNum.size() + " nucleotides found");
		return null;		
	}	
	
	public static List<Integer> getGeneCodonPositions(int isoformPos, List<Entry<Integer,Integer>> positionsOfIsoformOnReferencedGene) {
		int nu1Num = isoformPos * 3 - 3;  
		//System.out.println("nu1Num:" + nu1Num);
		int lowNum = 0;
		List<Integer> result = new ArrayList<Integer>();
		for (Entry<Integer,Integer> e: positionsOfIsoformOnReferencedGene) {
			int nu1Pos = e.getKey();
			int nu2Pos = e.getValue();
			int highNum = lowNum + nu2Pos - nu1Pos ;
			//System.out.println("nu1Pos:"+ nu1Pos + " nu2Pos:" + nu2Pos + " lowNum:" + lowNum + " highNum:" + highNum);
			while (true) {
				int nuIndex = result.size();
				int nuNum = nu1Num + nuIndex;
				//System.out.println("nuNum:" + nuNum);
				if (nuNum < lowNum || nuNum > highNum) break;
				result.add(nu1Pos + nuNum-lowNum);
				if (result.size()==3) return result; 
			}
			lowNum=highNum + 1;
		}
		return result;		
	}	
}
