package org.nextprot.api.demo.sparql.queries.service.impl;

import java.util.List;

import org.nextprot.api.demo.sparql.queries.domain.DemoSparqlQuery;
import org.nextprot.api.demo.sparql.queries.service.DemoSparqlService;
import org.nextprot.api.demo.sparql.queries.utils.DemoSparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemoSparqlServiceImpl implements DemoSparqlService{
	
	@Autowired DemoSparqlDictionary demoSparqlDictionary;
	
	public List<DemoSparqlQuery> getDemoSparqlQueries() {
		return demoSparqlDictionary.getDemoSparqlList();
	}

	@Override
	public void relaodDemoSparqlQueries() {
		demoSparqlDictionary.reloadDemoQueries();
	}
	

}
