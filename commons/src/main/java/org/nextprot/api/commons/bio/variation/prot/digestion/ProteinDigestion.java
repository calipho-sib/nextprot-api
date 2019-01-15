package org.nextprot.api.commons.bio.variation.prot.digestion;

import org.expasy.mzjava.proteomics.mol.Peptide;

import java.util.List;

public interface ProteinDigestion {

	void digest(String isoformAccession, List<Peptide> peptides);
	List<String> getIsoformSequences(String isoformAccession);
}
