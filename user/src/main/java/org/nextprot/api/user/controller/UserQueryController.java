package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for operating (CRUD) on user queries (SPARQL)
 * 
 * @author dteixeira
 */
@Lazy
@Controller
@Api(name = "User Queries", description = "Method to manipulate user queries (SPARQL)", role = "ROLE_USER")
public class UserQueryController {

	@Autowired
	private UserQueryService userQueryService;

	@RequestMapping(value = "/queries/public", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getTutorialQueries() {
		return userQueryService.getTutorialQueries();
	}

	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.GET })
	@ResponseBody
	public UserQuery getUserQuery(@PathVariable("username") String username, @PathVariable("id") String id) {
		return userQueryService.getUserQueryById(Long.valueOf(id));
	}
	
	@RequestMapping(value = "/user/{username}/query", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getUserQueries(@PathVariable("username") String username) {
		return userQueryService.getUserQueries(username);
	}

	@RequestMapping(value = "/user/{username}/query", method = { RequestMethod.POST })
	@ResponseBody
	public UserQuery createAdvancedQuery(@RequestBody UserQuery userQuery, @PathVariable("username") String username) {
		System.err.println(userQuery);
		userQuery.setOwner(username);
		return userQueryService.createUserQuery(userQuery);
	}

	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserQuery updateAdvancedQuery(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserQuery advancedUserQuery, Model model) {

		// Never trust what the users sends to you! Set the correct username, so it will be verified by the service,
		UserQuery q = userQueryService.getUserQueryById(advancedUserQuery.getUserQueryId());
		advancedUserQuery.setOwner(q.getOwner());
		advancedUserQuery.setOwnerId(q.getOwnerId());

		return userQueryService.updateUserQuery(advancedUserQuery);
	}

	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.DELETE })
	public void deleteUserQuery(@PathVariable("username") String username, @PathVariable("id") String id, Model model) {

		// Never trust what the users sends to you! Send the query with the correct username, so it will be verified by the service,
		UserQuery q = userQueryService.getUserQueryById(Long.parseLong(id));
		userQueryService.deleteUserQuery(q);

	}
	


}
