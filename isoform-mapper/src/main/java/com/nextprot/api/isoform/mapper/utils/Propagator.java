package com.nextprot.api.isoform.mapper.utils;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

public class Propagator {

	private Entry entry;
	
	public Propagator(Entry entry) {
		this.entry=entry;
	}
	
	public Entry getEntry() {
		return this.getEntry();
	}
	
	public static CodonNucleotidePositions getMasterCodonNucleotidesPositions(int aaPosition, Isoform isoform) {
		return PropagatorCore.getCodonNucleotidesPositionsInRanges(aaPosition, isoform.getMasterMapping());
	}
	
	public CodonNucleotidePositions getMasterCodonNucleotidesPositions(int aaPosition, String isoformName) {
		Isoform isoform = EntryIsoform.getIsoformByName(entry, isoformName);
		return PropagatorCore.getCodonNucleotidesPositionsInRanges(aaPosition, isoform.getMasterMapping());
	}
	
	public static CodonNucleotideIndices getMasterCodonNucleotidesIndices(CodonNucleotidePositions nuPositions, Isoform isoform) {
		return PropagatorCore.getCodonNucleotidesIndicesInRanges(nuPositions, isoform.getMasterMapping());
	}
	
	public CodonNucleotideIndices getMasterCodonNucleotidesIndices(CodonNucleotidePositions nuPositions, String isoformName) {
		Isoform isoform = EntryIsoform.getIsoformByName(entry, isoformName);
		return PropagatorCore.getCodonNucleotidesIndicesInRanges(nuPositions, isoform.getMasterMapping());
	}
	
	public static Integer getProjectedPosition(Isoform srcIsoform, int srcPosition, Isoform trgIoform) {
		CodonNucleotidePositions cnPositions = PropagatorCore.getCodonNucleotidesPositionsInRanges(srcPosition, srcIsoform.getMasterMapping());
		CodonNucleotideIndices cnIndices = PropagatorCore.getCodonNucleotidesIndicesInRanges(cnPositions, trgIoform.getMasterMapping());
		return cnIndices.getAminoAcidPosition();
	}
	
	/**
	 * Check that we have amino acid aa(s) in isoform sequence at position pos.
	 * If aa iss null or empty string we just check that position is < sequence lenght
	 * @param isoform
	 * @param pos position according to bio standard (first pos = 1)
	 * @param aa 0, 1 or more amino acids (1 char / aa)
	 * @return
	 */
	public static boolean checkAminoAcidPosition(Isoform isoform, int pos, String aa) {
		return PropagatorCore.checkAminoAcidPosition(isoform.getSequence(), pos, aa);
	}
}
