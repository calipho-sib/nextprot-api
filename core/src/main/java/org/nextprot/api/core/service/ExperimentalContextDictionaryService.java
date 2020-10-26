package org.nextprot.api.core.service;

import java.util.Map;

import org.nextprot.api.core.domain.ExperimentalContext;

public interface ExperimentalContextDictionaryService {

	Map<Long,ExperimentalContext> getAllExperimentalContexts();
	ExperimentalContext getExperimentalContextByProperties(long tissueId, long developmentalStageId, long detectionMethodId);
}
