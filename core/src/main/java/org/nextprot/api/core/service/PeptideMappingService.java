package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface PeptideMappingService {

	List<PeptideMapping> findNaturalPeptideMappingByMasterUniqueName(@ValidEntry String uniqueName);
	List<PeptideMapping> findSyntheticPeptideMappingByMasterUniqueName(@ValidEntry String uniqueName);
	List<String> findAllPeptideNamesByMasterId(String uniqueName);

}
