package org.nextprot.api.etl.service;

import org.nextprot.commons.statements.constants.NextProtSource;

public interface StatementETLService {

    String etlStatements(NextProtSource source);
    
}
