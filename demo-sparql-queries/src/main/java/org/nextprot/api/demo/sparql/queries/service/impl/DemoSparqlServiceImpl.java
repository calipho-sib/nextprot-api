package org.nextprot.api.demo.sparql.queries.service;

import java.util.List;

import org.nextprot.api.demo.example.queries.DemoSparqlDictionary;
import org.nextprot.api.demo.example.queries.DemoSparqlQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class DemoSparqlServiceImpl implements DemoSparqlService{
	
	@Autowired DemoSparqlDictionary demoSparqlDictionary;
	
	public List<DemoSparqlQuery> getDemoSparqlQueries() {
		return demoSparqlDictionary.getDemoSparqlList();
	}
	

}
