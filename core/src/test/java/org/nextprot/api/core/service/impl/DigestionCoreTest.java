package org.nextprot.api.core.service.impl;

import java.util.List;

import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.Protein;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;
import org.junit.Test;

public class DigestionCoreTest  {

	@Test
	public void shouldMakeUsHappy() {
		Protein prot = new Protein("ACC1", "ABCKKDEFKGHI");
		//Protein prot = new Protein("ACC1", "MALWMRLLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGERGFFYTPKTRREAEDLQVGQVELGGGPGAGSLQPLALEGSLQKRGIVEQCCTSICSLYQLENYCN");
		
        List<Peptide> digests = new ProteinDigester.Builder(Protease.TRYPSIN).missedCleavageMax(0).build().digest(prot);
        for (Peptide p: digests) {
        	System.out.println(p.toSymbolString() + " - " + p.getProducedWithMisceleavageCount());
        }
        
	}
	
}