package org.nextprot.api.core.service;

import java.util.Map;
import java.util.Set;
import org.nextprot.api.core.domain.SequenceUnicity;

public interface SequenceUnicityService {

	SequenceUnicity getSequenceUnicityFromMappingIsoforms(Set<String> isoformAcs);

	Map<String, SequenceUnicity> getPeptideNameUnicityMap();

	Map<String, SequenceUnicity> getAntibodyNameUnicityMap();
}
