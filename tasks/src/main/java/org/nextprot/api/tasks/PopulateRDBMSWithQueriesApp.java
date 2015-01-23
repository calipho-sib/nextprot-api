package org.nextprot.api.tasks;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PopulateRDBMSWithQueriesApp {

	public static void main(String[] args) {

		System.setProperty("spring.profiles.active", "pro");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/commons-context.xml", "spring/demo-queries-context.xml");

		/*DemoSparqlService sparqlService = ctx.getBean(DemoSparqlService.class);
		UserQueryService userQueryService = ctx.getBean(UserQueryService.class);
		UserService userService = ctx.getBean(UserService.class);

		for (UserQuery uq : userQueryService.getTutorialQueries()) {
			userQueryService.deleteUserQuery(uq);
			System.err.println("deleting" + uq.getUserQueryId());
		}

		userService.loadUserByUsername("nextprot");
		userService.loadUserByUsername("ddtxra@gmail.com");
		userService.loadUserByUsername("evaleto@gmail.com");

		int i = 0;
		for (UserQuery q : sparqlService.getDemoSparqlQueries()) {

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
			uq.setSparql(q.getSparql());
			uq.setPublished(true);
			if (q.getTags() != null) {
				uq.setTags(Sets.newHashSet(q.getTags()));
			}

			userQueryService.createUserQuery(uq);
		}*/
	}

}
