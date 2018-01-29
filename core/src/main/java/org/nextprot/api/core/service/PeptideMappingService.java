package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;

import java.util.List;

public interface PeptideMappingService {

	List<Annotation> findNaturalPeptideMappingAnnotationsByMasterUniqueName(@ValidEntry String uniqueName);
	List<Annotation> findSyntheticPeptideMappingAnnotationsByMasterUniqueName(@ValidEntry String uniqueName);
	
		
}
