package org.nextprot.api.core.service;

import java.util.List;

import org.nextprot.api.core.domain.ExperimentalContext;

public interface ExperimentalContextService {

	//List<ExperimentalContext> findExperimentalContextsByIds(List<Long> ids);
	List<ExperimentalContext> findExperimentalContextsByEntryName(String entryName);
	List<ExperimentalContext> findAllExperimentalContexts();

}
