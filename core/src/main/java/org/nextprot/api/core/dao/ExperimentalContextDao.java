package org.nextprot.api.core.dao;

import java.sql.SQLException;
import java.util.List;

import org.nextprot.api.commons.bio.experimentalcontext.ExperimentalContextStatement;
import org.nextprot.api.core.app.StatementSource;
import org.nextprot.api.core.domain.ExperimentalContext;


public interface ExperimentalContextDao {

	List<ExperimentalContext> findExperimentalContextsByIds(List<Long> ids);
	List<ExperimentalContext> findAllExperimentalContexts();

	// Loads a set of experimental context statements and returns the corresponding SQL Statement
	String loadExperimentalContexts(StatementSource source, List<ExperimentalContextStatement> experimentalContexts, boolean load) throws SQLException;
	
}
