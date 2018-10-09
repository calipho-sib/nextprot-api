package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ProteinExistenceInferred;

public interface ProteinExistenceInferenceService {

	ProteinExistenceInferred inferProteinExistence(String entryAccession);

	boolean cannotBePromotedAccordingToRule1(String entryAccession);

	boolean promotedAccordingToRule2(String entryAccession);

	boolean promotedAccordingToRule3(String entryAccession);

	boolean promotedAccordingToRule4(String entryAccession);

	boolean promotedAccordingToRule5(String entryAccession);

	boolean promotedAccordingToRule6(String entryAccession);

    boolean promotedAccordingToRule7(String entryAccession);
}
