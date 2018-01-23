package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ProteinExistenceInferred;

public interface ProteinExistenceInferenceService {

	/**
	 * @return the calculated protein existence based on rules
	 */
	ProteinExistenceInferred inferProteinExistence(String entryAccession);

	boolean proteinExistencePromoted(String entryAccession);

	boolean cannotBePromotedAccordingToRule1(String entryAccession);

	// Spec: Entry must have at least 2 proteotypic peptides of quality GOLD, 9 or more amino acids in length,
	// which must differ by at least 1 amino acid and not overlap (i.e. one of the peptides must not be included in the other)
	boolean promotedAccordingToRule2(String entryAccession);

	// Spec: Entry must have an expression information annotation containing the text "(at protein level)"
	// with evidence assigned by neXtProt of quality GOLD AND ECO experimental evidence (or child thereof)
	boolean promotedAccordingToRule3(String entryAccession);

	boolean promotedAccordingToRule4(String entryAccession);

	boolean promotedAccordingToRule5(String entryAccession);

	boolean promotedAccordingToRule6(String entryAccession);
}
