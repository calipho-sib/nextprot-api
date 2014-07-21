package org.nextprot.api.service;

import java.util.List;

import org.nextprot.api.domain.ExperimentalContext;

public interface ExperimentalContextService {

	List<ExperimentalContext> findExperimentalContextsByIds(List<String> ids);
	List<ExperimentalContext> findAllExperimentalContexts();

}
