package org.nextprot.api.commons.bio.variation.prot.digestion;

import org.expasy.mzjava.proteomics.mol.Peptide;

import java.util.List;

public interface ProteinDigestion {

	void digest(String isoformAccession, List<Peptide> peptides) throws MissingIsoformException;
	List<String> getIsoformSequences(String isoformAccession) throws MissingIsoformException;

	class MissingIsoformException extends Exception {

		public MissingIsoformException(String accession) {

			super("isoform "+ accession + " was not found");
		}
	}
}
