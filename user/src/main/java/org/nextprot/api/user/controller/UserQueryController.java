package org.nextprot.api.user.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
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
@Api(name = "User Queries", description = "Method to manipulate personal user queries when authenticated", group="User")
@PreAuthorize("hasRole('ROLE_USER')")
@ApiAuthBasic(roles={"ROLE_USER"})
public class UserQueryController {

	@Autowired
	private UserQueryService userQueryService;

	// Collections /////////////////
	@ApiMethod(verb = ApiVerb.GET, description = "Gets user queries for the current logged user and all the tutorials queries as well, If snorql parameter is set, snorql specific queries should also be retrieved", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/queries", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getTutorialQueries(@RequestParam(value="snorql", required=false) Boolean snorql) {
		return userQueryService.getUserQueries(NPSecurityContext.getCurrentUser());
	}
	
	// Elements (CRUD) /////////////////

	// CREATE
	@ApiMethod(verb = ApiVerb.POST, description = "Creates an advanced query for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/queries", method = { RequestMethod.POST })
	@ResponseBody
	public UserQuery createAdvancedQuery(@RequestBody UserQuery userQuery) {
		return userQueryService.createUserQuery(userQuery);
	}

	// READ
	@ApiMethod(verb = ApiVerb.GET, description = "Gets a user query by its private or public id. Only if you are authenticated and authorized you can access the list with its private id.", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = "/user/me/queries/{id}", method = { RequestMethod.GET })
	@ResponseBody
	public UserQuery getUserQuery(@ApiPathParam(name = "id", description = "The private or public id", allowedvalues = { "NXQ_00001" }) @PathVariable("id") Integer id) {
		return userQueryService.getUserQueryById(id);
	}

	// UPDATE
	@ApiMethod(path = "/user/me/queries/{id}", verb = ApiVerb.PUT, description = "Updates an advanced query for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/queries/{id}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserQuery updateAdvancedQuery(@PathVariable("id") String id, @RequestBody UserQuery advancedUserQuery, Model model) {

		// Never trust what the users sends to you! Set the correct username, so it will be verified by the service, 
		//TODO Is this done on the aspect
		UserQuery q = userQueryService.getUserQueryById(advancedUserQuery.getUserQueryId());
		advancedUserQuery.setOwner(q.getOwner());
		advancedUserQuery.setOwnerId(q.getOwnerId());

		return userQueryService.updateUserQuery(advancedUserQuery);
	}

	// DELETE
	@ApiMethod(verb = ApiVerb.DELETE, description = "Deletes an advanced query for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/queries/{id}", method = { RequestMethod.DELETE })
	public void deleteUserQuery(@PathVariable("id") String id, Model model) {
		// Never trust what the users sends to you! Send the query with the correct username, so it will be verified by the service,
		//TODO Is this done on the aspect
		UserQuery q = userQueryService.getUserQueryById(Long.parseLong(id));
		userQueryService.deleteUserQuery(q);

	}
	
}
