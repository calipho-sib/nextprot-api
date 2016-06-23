package com.nextprot.api.isoform.mapper.utils;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;

/**
 * Responsible to search a position from a sequence isoform matching the corresponding master (gene level)
 * or other sequence isoforms (protein level)
 */
public class IsoformSequencePositionMapper {

	private Entry entry;
	
	public IsoformSequencePositionMapper(Entry entry) {
		this.entry=entry;
	}
	
	public Entry getEntry() {
		return this.getEntry();
	}
	
	public static CodonNucleotidePositions getMasterCodonNucleotidesPositions(int aaPosition, Isoform isoform) {
		return SequencePositionMapper.getCodonNucleotidesPositionsInRanges(aaPosition, isoform.getMasterMapping());
	}
	
	public CodonNucleotidePositions getMasterCodonNucleotidesPositions(int aaPosition, String isoformName) {
		Isoform isoform = EntryIsoform.getIsoformByName(entry, isoformName);
		return SequencePositionMapper.getCodonNucleotidesPositionsInRanges(aaPosition, isoform.getMasterMapping());
	}
	
	public static CodonNucleotideIndices getMasterCodonNucleotidesIndices(CodonNucleotidePositions nuPositions, Isoform isoform) {
		return SequencePositionMapper.getCodonNucleotidesIndicesInRanges(nuPositions, isoform.getMasterMapping());
	}
	
	public CodonNucleotideIndices getMasterCodonNucleotidesIndices(CodonNucleotidePositions nuPositions, String isoformName) {
		Isoform isoform = EntryIsoform.getIsoformByName(entry, isoformName);
		return SequencePositionMapper.getCodonNucleotidesIndicesInRanges(nuPositions, isoform.getMasterMapping());
	}
	
	public static Integer getProjectedPosition(Isoform srcIsoform, int srcPosition, Isoform trgIoform) {
		CodonNucleotidePositions cnPositions = SequencePositionMapper.getCodonNucleotidesPositionsInRanges(srcPosition, srcIsoform.getMasterMapping());
		CodonNucleotideIndices cnIndices = SequencePositionMapper.getCodonNucleotidesIndicesInRanges(cnPositions, trgIoform.getMasterMapping());
		return cnIndices.getAminoAcidPosition();
	}

	public static boolean checkAminoAcidsFromPosition(Isoform isoform, int pos, String aa) {
		return SequencePositionMapper.checkAminoAcidsFromPosition(isoform.getSequence(), pos, aa);
	}

	public static boolean checkSequencePosition(Isoform isoform, int pos, boolean insertionMode) {
		return SequencePositionMapper.checkSequencePosition(isoform.getSequence(), pos, insertionMode);
	}
}
