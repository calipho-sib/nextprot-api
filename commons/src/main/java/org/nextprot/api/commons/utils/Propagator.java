package org.nextprot.api.commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Propagator {

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
