package org.nextprot.api.demo.sparql.queries;

import org.nextprot.api.demo.sparql.queries.domain.DemoSparqlQuery;
import org.nextprot.api.demo.sparql.queries.service.DemoSparqlService;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Sets;

public class PopulateRDBMSWithQueriesApp {

	public static void main(String[] args) {

		System.setProperty("spring.profiles.active", "pro");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/commons-context.xml", "spring/demo-queries-context.xml");

		DemoSparqlService sparqlService = ctx.getBean(DemoSparqlService.class);
		UserQueryService userQueryService = ctx.getBean(UserQueryService.class);
		UserService userService = ctx.getBean(UserService.class);

		for (UserQuery uq : userQueryService.getPublishedQueries()) {
			userQueryService.deleteUserQuery(uq);
			System.err.println("deleting" + uq.getUserQueryId());
		}

		userService.loadUserByUsername("nextprot");
		userService.loadUserByUsername("ddtxra@gmail.com");
		userService.loadUserByUsername("evaleto@gmail.com");

		int i = 0;
		for (DemoSparqlQuery q : sparqlService.getDemoSparqlQueries()) {

			i++;

			UserQuery uq = new UserQuery();
			String username = null;

			if ((i % 3) == 0) {
				username = "ddtxra@gmail.com";
			} else if ((i % 3) == 1) {
				username = "evaleto@gmail.com";
			} else {
				username = "nextprot";
			}

			uq.setOwner(username);
			uq.setOwnerId(userService.getUser(username).getId());

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
