package org.nextprot.api.etl.service;

import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementETLService {

	/**
	 * 
	 * @param source
	 * @return
	 */
    String etlStatements(NextProtSource source, boolean load);
    
}
