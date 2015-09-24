package org.nextprot.api.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PeptideUtils {

	private static final Log LOGGER = LogFactory.getLog(PeptideUtils.class);

	public static boolean isPeptideContainedInTheSequence(String peptide, String sequence, boolean modeIsoLeucine) {
		String sequenceToMatch = modeIsoLeucine? sequence.toUpperCase().replaceAll("I", "L") : sequence.toUpperCase();
		String peptideToMatch = modeIsoLeucine ? peptide.toUpperCase().replaceAll("I", "L") : peptide.toUpperCase();
		
		LOGGER.debug("Trying to find " + peptideToMatch + " inside " + sequenceToMatch);
		return sequenceToMatch.contains(peptideToMatch);
	}


}
