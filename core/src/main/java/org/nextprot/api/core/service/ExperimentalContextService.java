package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.ExperimentalContext;

public interface ExperimentalContextService {

	List<ExperimentalContext> findAllExperimentalContexts();
	List<ExperimentalContext> findExperimentalContextsByIds(Set<Long> ecIds);

	// This is only applies for the cases where there is a unique triple of (tissue_id, developmental_stage_id, detection_method_id)
	ExperimentalContext findExperimentalContextByProperties(long tissueId, long developmentalStageId, long detectionMethodId);
}
