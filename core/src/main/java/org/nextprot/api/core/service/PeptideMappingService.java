package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.PeptideMapping;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface PeptideMappingService {

	List<String> findAllPeptideNamesByMasterId(String uniqueName);

	// old interface (soon obsolete)
	List<PeptideMapping> findNaturalPeptideMappingByMasterUniqueName(@ValidEntry String uniqueName);
	List<PeptideMapping> findSyntheticPeptideMappingByMasterUniqueName(@ValidEntry String uniqueName);
	
	// new interface 
	List<Annotation> findNaturalPeptideMappingAnnotationsByMasterUniqueName(@ValidEntry String uniqueName);
	List<Annotation> findSyntheticPeptideMappingAnnotationsByMasterUniqueName(@ValidEntry String uniqueName);
	
	
}
