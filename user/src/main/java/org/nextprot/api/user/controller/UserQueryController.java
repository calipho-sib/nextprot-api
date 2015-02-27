package org.nextprot.api.user.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.utils.UserQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for operating (CRUD) on user queries (SPARQL)
 * 
 * @author dteixeira
 */
@Lazy
@Controller
@Api(name = "User Queries", description = "Method to manipulate user queries (SPARQL)", group="User")
public class UserQueryController {

	@Autowired
	private UserQueryService userQueryService;

	@ApiMethod(path = "/queries/tutorial", verb = ApiVerb.GET, description = "Get tutorial queries for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/queries/tutorial", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getTutorialQueries(@RequestParam(value="snorql", required=false) Boolean snorql) {

		//start with queries
		List<UserQuery> res = userQueryService.getTutorialQueries();

		//add user queries if logged (access db, but is cached with cache evict if the query is modified)
		if (NPSecurityContext.getCurrentUser() != null) { 
			res.addAll(userQueryService.getUserQueries(NPSecurityContext.getCurrentUser()));
		}

		//remove snorql queries if not specified
		if(snorql == null || !snorql){
			res = UserQueryUtils.removeQueriesContainingTag(res, "snorql-only");
		}
		
		return res;
	}
	
	@ApiMethod(path = "/user/{username}/query/{id}", verb = ApiVerb.GET, description = "Get user query", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.GET })
	@ResponseBody
	public UserQuery getUserQuery(@PathVariable("username") String username, @PathVariable("id") String id) {
		return userQueryService.getUserQueryById(Long.valueOf(id));
	}
	
	@ApiMethod(path = "/user/{username}/query/{id}", verb = ApiVerb.GET, description = "Get user queries", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/{username}/query", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getUserQueries(@PathVariable("username") String username) {
		return userQueryService.getUserQueries(username);
	}

	@ApiMethod(path = "/user/{username}/query", verb = ApiVerb.POST, description = "Creates an advanced query for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/query", method = { RequestMethod.POST })
	@ResponseBody
	public UserQuery createAdvancedQuery(@RequestBody UserQuery userQuery, @PathVariable("username") String username) {
		userQuery.setOwner(username);
		return userQueryService.createUserQuery(userQuery);
	}

	@ApiMethod(path = "/user/{username}/query/{id}", verb = ApiVerb.PUT, description = "Updates an advanced query for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserQuery updateAdvancedQuery(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserQuery advancedUserQuery, Model model) {

		// Never trust what the users sends to you! Set the correct username, so it will be verified by the service,
		UserQuery q = userQueryService.getUserQueryById(advancedUserQuery.getUserQueryId());
		advancedUserQuery.setOwner(q.getOwner());
		advancedUserQuery.setOwnerId(q.getOwnerId());

		return userQueryService.updateUserQuery(advancedUserQuery);
	}

	@ApiMethod(path = "/user/{username}/query/{id}", verb = ApiVerb.DELETE, description = "Deletes an advanced query for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/query/{id}", method = { RequestMethod.DELETE })
	public void deleteUserQuery(@PathVariable("username") String username, @PathVariable("id") String id, Model model) {

		// Never trust what the users sends to you! Send the query with the correct username, so it will be verified by the service,
		UserQuery q = userQueryService.getUserQueryById(Long.parseLong(id));
		userQueryService.deleteUserQuery(q);

	}
	
}
