package org.nextprot.api.web;

import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PopulateRDBMSWithProteinListsApp {

	public static void main(String[] args) {

		System.setProperty("spring.profiles.active", "pro");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/core-context.xml");
		UserProteinListService userProteinListService = ctx.getBean(UserProteinListService.class);
		UserService userService = ctx.getBean(UserService.class);

		String username = "ddtxra@gmail.com";
		
		for (int i = 0; i < 10; i++) {
			UserProteinList pl = new UserProteinList();
			pl.setName("some name" + i);
			pl.setDescription("some description" + i);
			pl.setEntriesCount(5);
			pl.setOwner(username);
			pl.setOwnerId(userService.getUser(username).getId());
			
			userProteinListService.createUserProteinList(pl);
		}
		
		ctx.close();

	}
}
