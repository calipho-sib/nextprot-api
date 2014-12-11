package org.nextprot.api.demo.sparql.queries;

import org.nextprot.api.demo.sparql.queries.domain.DemoSparqlQuery;
import org.nextprot.api.demo.sparql.queries.service.DemoSparqlService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class GenerateMarkdownExampleQueriesApp {

	public static void main(String[] args) {

		System.setProperty("spring.profiles.active", "dev");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/commons-context.xml", "spring/demo-queries-context.xml");

		DemoSparqlService sparqlService = ctx.getBean(DemoSparqlService.class);
		for (DemoSparqlQuery q : sparqlService.getDemoSparqlQueries()) {
			System.out.println(q.getTitle());
			System.out.println("\n");
			System.out.println("```");
			System.out.println(q.getQuery()); // should remove all comments
			System.out.println("```");
			System.out.println("\n");

		}
	}

}
