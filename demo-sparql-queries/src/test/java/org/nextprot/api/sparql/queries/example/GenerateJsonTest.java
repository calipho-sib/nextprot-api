package org.nextprot.api.sparql.queries.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nextprot.api.demo.example.queries.DemoSparqlDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/demo-queries-context.xml")
public class GenerateJsonTest{
	
	@Autowired private DemoSparqlDictionary demoSparqlDictionary;
	
	@Test
	public void test() {
		String s = demoSparqlDictionary.getDemoQuery("Q005-located-in-mitochondrion-and-lack-a-transit-peptide");
		System.out.println(s);
		System.err.println("Yooo");
	}

}
