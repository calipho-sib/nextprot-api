package org.nextprot.api.core.service;

import java.util.Map;
import java.util.Set;
import org.nextprot.api.core.domain.PeptideUnicity;

public interface PeptideUnicityService {

	PeptideUnicity getPeptideUnicityFromMappingIsoforms(Set<String> isoformAcs);
	Map<String,PeptideUnicity> getPeptideNameUnicityMap();
		
}