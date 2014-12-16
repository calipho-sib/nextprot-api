package org.nextprot.api.demo.sparql.queries.service;

import java.util.List;

import org.nextprot.api.user.domain.UserQuery;

public interface DemoSparqlService {
	
	List<UserQuery> getDemoSparqlQueries();

	void relaodDemoSparqlQueries();

}
