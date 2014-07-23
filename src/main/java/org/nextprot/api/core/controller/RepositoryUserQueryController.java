package org.nextprot.api.core.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.UserQuery;
import org.nextprot.api.core.service.RepositoryUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for operating (CRUD) on user queries (SPARQL)
 * 
 * @author dteixeira
 */
@Lazy
@Controller
public class RepositoryUserQueryController {

	private final Log Logger = LogFactory.getLog(RepositoryUserQueryController.class);

	@Autowired
	private RepositoryUserQueryService repositoryUserQueryService;

	@RequestMapping(value = "/user/{username}/advanced-user-query", method = { RequestMethod.GET })
	public List<UserQuery> getUserQueries(@PathVariable("username") String username) {
		return repositoryUserQueryService.getUserQueries(username);
	}

	@RequestMapping(value = "/user/advanced-public-query", method = { RequestMethod.GET })
	public List<UserQuery> getPublicQueries() {
		return repositoryUserQueryService.getPublicQueries();
	}

	@RequestMapping(value = "/user/advanced-nextprot-query", method = { RequestMethod.GET })
	public List<UserQuery> getNextprotQueries() {
		return repositoryUserQueryService.getNextprotQueries();
	}

	@RequestMapping(value = "/user/{username}/advanced-user-query", method = { RequestMethod.POST })
	@ResponseBody
	public UserQuery createAdvancedQuery(@RequestBody UserQuery advancedUserQuery, @PathVariable("username") String username) {
		advancedUserQuery.setUsername(username);
		return repositoryUserQueryService.createUserQuery(advancedUserQuery);
	}

	@RequestMapping(value = "/user/{username}/advanced-user-query/{id}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserQuery updateAdvancedQuery(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserQuery advancedUserQuery, Model model) {

		// Never trust what the users sends to you! Set the correct username, so it will be verified by the service,
		UserQuery q = repositoryUserQueryService.getUserQueryById(advancedUserQuery.getUserQueryId());
		advancedUserQuery.setUsername(q.getUsername());

		return repositoryUserQueryService.updateUserQuery(advancedUserQuery);
	}

	@RequestMapping(value = "/user/{username}/advanced-user-query/{id}", method = { RequestMethod.DELETE })
	public Model deleteAdvancedQuery(@PathVariable("username") String username, @PathVariable("id") String id, Model model) {

		// Never trust what the users sends to you! Send the query with the correct username, so it will be verified by the service,
		long qid = Long.parseLong(id);
		UserQuery q = repositoryUserQueryService.getUserQueryById(qid);
		repositoryUserQueryService.deleteUserQuery(q);
		return model;

	}

}
