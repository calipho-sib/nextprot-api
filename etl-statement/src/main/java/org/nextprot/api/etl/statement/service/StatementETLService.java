package org.nextprot.api.etl.statement.service;

import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementETLService {

    String etlStatements(NextProtSource source);
    
}
