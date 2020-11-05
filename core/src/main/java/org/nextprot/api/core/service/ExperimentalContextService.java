package org.nextprot.api.core.service;

import java.util.List;
import java.util.Set;

import org.nextprot.api.core.domain.ExperimentalContext;

public interface ExperimentalContextService {

	List<ExperimentalContext> findAllExperimentalContexts();
	List<ExperimentalContext> findExperimentalContextsByIds(Set<Long> ecIds);
	ExperimentalContext findExperimentalContextByMd5(String md5);
}
