package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.annotation.ValidEntry;

public interface PeptideMappingService {

	List<Annotation> findNaturalPeptideMappingAnnotationsByMasterUniqueName(@ValidEntry String uniqueName);
	List<Annotation> findSyntheticPeptideMappingAnnotationsByMasterUniqueName(@ValidEntry String uniqueName);
		
}
