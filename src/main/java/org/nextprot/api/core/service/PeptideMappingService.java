package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface PeptideMappingService {

	List<PeptideMapping> findPeptideMappingByMasterId(Long id);
	
	List<PeptideMapping> findPeptideMappingByUniqueName(@ValidEntry String uniqueName);
}
