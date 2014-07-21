package org.nextprot.api.dao;

import java.util.List;

import org.nextprot.api.domain.ExperimentalContext;


public interface ExperimentalContextDao {

	List<ExperimentalContext> findExperimentalContextsByIds(List<String> ids);
	List<ExperimentalContext> findAllExperimentalContexts();
	
}
