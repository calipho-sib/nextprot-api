package org.nextprot.api.sparql.queries.example;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.demo.sparql.queries.domain.DemoSparqlQuery;
import org.nextprot.api.demo.sparql.queries.utils.DemoSparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/demo-queries-context.xml")
public class GenerateJsonTest{
	
	@Autowired private DemoSparqlDictionary demoSparqlDictionary;
	
	@Test
	public void test() {
		List<DemoSparqlQuery> queries = demoSparqlDictionary.getDemoSparqlList();
		for(DemoSparqlQuery query : queries){
			System.out.println(query);
		}
	}

}
