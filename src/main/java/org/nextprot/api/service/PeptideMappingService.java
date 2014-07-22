package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.PeptideMapping;
import org.nextprot.api.service.aop.ValidEntry;

public interface PeptideMappingService {

	List<PeptideMapping> findPeptideMappingByMasterId(Long id);
	
	List<PeptideMapping> findPeptideMappingByUniqueName(@ValidEntry String uniqueName);
}
