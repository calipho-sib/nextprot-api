package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.PeptideUnicity;

import java.util.Set;

public interface PeptideUnicityService {

	PeptideUnicity getPeptideUnicityFromMappingIsoforms(Set<String> isoformAcs);
		
}
