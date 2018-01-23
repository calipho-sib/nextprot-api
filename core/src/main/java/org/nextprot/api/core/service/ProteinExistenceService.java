package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.ProteinExistences;

public interface ProteinExistenceService {

	ProteinExistences getProteinExistences(String entryAccession);
}
