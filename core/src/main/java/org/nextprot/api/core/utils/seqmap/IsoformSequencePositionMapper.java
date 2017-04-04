package org.nextprot.api.core.utils.seqmap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.Isoform;

/**
 * Responsible to search a position from a sequence isoform matching the corresponding master (gene level)
 * or other sequence isoforms (protein level)
 */
public class IsoformSequencePositionMapper {

    private static final Log logger = LogFactory.getLog(IsoformSequencePositionMapper.class);

	public static GeneMasterCodonPosition getCodonPositionsOnMaster(int aaPosition, Isoform isoform) {
		logger.debug("gettingCodonPositionOnMaster for isoform " + isoform.getIsoformAccession() + " and aa position " + aaPosition);
		return SequencePositionMapper.getCodonPositionOnMaster(aaPosition, isoform.getMasterMapping());
	}
	
	public static CodonNucleotideIndices getCodonNucleotideIndices(GeneMasterCodonPosition nuPositions, Isoform isoform) {
		logger.debug("getCodonNucleotideIndices for isoform " + isoform.getIsoformAccession()  + " and nu positions " + nuPositions );
		return SequencePositionMapper.getCodonNucleotideIndices(nuPositions, isoform.getMasterMapping());
	}

	/**
	 * Computed the projected position in srcIsoform to trgIsoform
	 * @param srcIsoform the source isoform
	 * @param srcPosition the aa position on the src isoform
	 * @param trgIsoform the isoform to project position on
     * @return a position on target isoform or null if cannot project
     */
	public static Integer getProjectedPosition(Isoform srcIsoform, int srcPosition, Isoform trgIsoform) {
		logger.debug("gettingProjectedPosition of aa at pos " + srcPosition + " of isoform " + srcIsoform.getIsoformAccession()  + " on isoform " + trgIsoform.getIsoformAccession() );
		
		logger.debug("gettingCodonPositionOnMaster for isoform " + srcIsoform.getIsoformAccession() + " and aa position " + srcPosition);
		GeneMasterCodonPosition codonPositionOnMaster =
				SequencePositionMapper.getCodonPositionOnMaster(srcPosition, srcIsoform.getMasterMapping());
		
		logger.debug("getCodonNucleotideIndices for isoform " + trgIsoform.getIsoformAccession()  + " and nu positions " + codonPositionOnMaster );
		CodonNucleotideIndices codonPositionOnTranscript =
				SequencePositionMapper.getCodonNucleotideIndices(codonPositionOnMaster, trgIsoform.getMasterMapping());

		return codonPositionOnTranscript.getAminoAcidPosition();
	}

	public static boolean checkAminoAcidsFromPosition(Isoform isoform, int pos, String aa) {
		return SequencePositionMapper.checkAminoAcidsFromPosition(isoform.getSequence(), pos, aa);
	}

	public static boolean checkSequencePosition(Isoform isoform, int pos, boolean insertionMode) {
		return SequencePositionMapper.checkSequencePosition(isoform.getSequence(), pos, insertionMode);
	}


}
