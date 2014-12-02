package org.nextprot.api.demo.sparql.queries;

import org.nextprot.api.demo.sparql.queries.domain.DemoSparqlQuery;
import org.nextprot.api.demo.sparql.queries.service.DemoSparqlService;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Sets;

public class PopulateRDBMSWithQueriesApp {

	public static void main(String[] args) {

		System.setProperty("spring.profiles.active", "dev");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/commons-context.xml", "spring/demo-queries-context.xml");

		DemoSparqlService sparqlService = ctx.getBean(DemoSparqlService.class);
		UserQueryService userQueryService = ctx.getBean(UserQueryService.class);

		for (DemoSparqlQuery q : sparqlService.getDemoSparqlQueries()) {

			UserQuery uq = new UserQuery();
			uq.setOwner("ddtxra@gmail.com");
			uq.setOwnerId(1);
			uq.setTitle(q.getTitle());
			uq.setSparql(q.getQuery());
			uq.setPublished(true);
			if (q.getTags() != null) {
				uq.setTags(Sets.newHashSet(q.getTags().split(",")));
			}

			userQueryService.createUserQuery(uq);
		}
	}

}
