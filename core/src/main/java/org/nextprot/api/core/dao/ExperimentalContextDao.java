package org.nextprot.api.core.dao;

import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.bio.experimentalcontext.ExperimentalContextStatement;
import org.nextprot.api.core.domain.ExperimentalContext;


public interface ExperimentalContextDao {

	List<ExperimentalContext> findExperimentalContextsByIds(List<Long> ids);
	List<ExperimentalContext> findAllExperimentalContexts();
	ExperimentalContext findExperimentalContextByProperties(long tissueId, long developmentalStageId, long detectionMethodId);

	// Loads a set of experimental context statements and returns the corresponding SQL Statement
	String loadExperimentalContexts(List<ExperimentalContextStatement> experimentalContexts, boolean erase) throws SQLException;
	
}
