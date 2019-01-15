package org.nextprot.api.commons.bio.variation.prot.digestion;

import org.expasy.mzjava.proteomics.mol.Peptide;
import org.expasy.mzjava.proteomics.mol.Protein;
import org.expasy.mzjava.proteomics.mol.digest.ProteinDigester;

import java.util.List;

public abstract class AminoAcidSequenceDigestion implements ProteinDigestion {

	private final ProteinDigester digester;

	public AminoAcidSequenceDigestion(ProteinDigester digester) {

		this.digester = digester;
	}

	@Override
	public void digest(String isoformAccession, List<Peptide> peptides) {

		getIsoformSequences(isoformAccession).forEach(sequence -> digester.digest(new Protein(isoformAccession, sequence), peptides));
	}
}