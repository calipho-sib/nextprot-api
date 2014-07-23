package org.nextprot.api.core.dao;

import java.util.List;

import org.nextprot.api.core.domain.ExperimentalContext;


public interface ExperimentalContextDao {

	List<ExperimentalContext> findExperimentalContextsByIds(List<String> ids);
	List<ExperimentalContext> findAllExperimentalContexts();
	
}
