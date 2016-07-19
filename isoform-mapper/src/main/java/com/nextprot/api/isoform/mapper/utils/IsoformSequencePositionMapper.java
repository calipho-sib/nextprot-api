package com.nextprot.api.isoform.mapper.utils;

import org.nextprot.api.core.domain.Isoform;

/**
 * Responsible to search a position from a sequence isoform matching the corresponding master (gene level)
 * or other sequence isoforms (protein level)
 */
public class IsoformSequencePositionMapper {

	public static GeneMasterCodonPosition getCodonPositionsOnMaster(int aaPosition, Isoform isoform) {
		return SequencePositionMapper.getCodonPositionOnMaster(aaPosition, isoform.getMasterMapping());
	}
	
	public static TranscriptCodonPosition getTranscriptCodon(GeneMasterCodonPosition nuPositions, Isoform isoform) {
		return SequencePositionMapper.getCodonPositionOnIsoformTranscript(nuPositions, isoform.getMasterMapping());
	}
	
	public static Integer getProjectedPosition(Isoform srcIsoform, int srcPosition, Isoform trgIsoform) {

		GeneMasterCodonPosition codonPositionOnMaster =
				SequencePositionMapper.getCodonPositionOnMaster(srcPosition, srcIsoform.getMasterMapping());

		TranscriptCodonPosition codonPositionOnTranscript =
				SequencePositionMapper.getCodonPositionOnIsoformTranscript(codonPositionOnMaster, trgIsoform.getMasterMapping());

		return codonPositionOnTranscript.getAminoAcidPosition();
	}

	public static boolean checkAminoAcidsFromPosition(Isoform isoform, int pos, String aa) {
		return SequencePositionMapper.checkAminoAcidsFromPosition(isoform.getSequence(), pos, aa);
	}

	public static boolean checkSequencePosition(Isoform isoform, int pos, boolean insertionMode) {
		return SequencePositionMapper.checkSequencePosition(isoform.getSequence(), pos, insertionMode);
	}


}
