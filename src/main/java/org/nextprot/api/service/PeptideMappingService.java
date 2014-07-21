package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.annotation.ValidEntry;
import org.nextprot.api.domain.PeptideMapping;

public interface PeptideMappingService {

	List<PeptideMapping> findPeptideMappingByMasterId(Long id);
	
	List<PeptideMapping> findPeptideMappingByUniqueName(@ValidEntry String uniqueName);
}
