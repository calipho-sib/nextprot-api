package org.nextprot.api.user.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.utils.UserQueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
@Api(name = "Queries", description = "Method to access queries without authentication (SPARQL)", group="Sparql Queries")
public class PublicQueryController {

	@Autowired
	private UserQueryService userQueryService;

	// Collections /////////////////
	@ApiMethod(verb = ApiVerb.GET, description = "Gets all public queries plus user queries if the user is currently logged in. Also if snorql parameter is set, snorql specific queries will also be retrieved", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/queries", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getQueriesIHaveAccess(@RequestParam(value="snorql", required=false) Boolean snorql) {

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
	
	
	//Not really used, just to show the user...
	@ApiMethod(verb = ApiVerb.GET, description = "Gets all tutorial queries", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/queries/tutorial", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserQuery> getTutorialQueries() {
		return userQueryService.getTutorialQueries();
	}
	
	
	// Gets query by public id /////////////////
	@ApiMethod(verb = ApiVerb.GET, description = "Gets user queries  the current logged user and all the tutorials queries as well, If snorql parameter is set, snorql specific queries should also be retrieved", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/queries/{id}", method = { RequestMethod.GET })
	@ResponseBody
	public UserQuery getQueriesByItsPublicId(@ApiPathParam(name = "id", description = "The private or public id", allowedvalues = { "NXQ_00001" }) @PathVariable("id") String id) {
		return userQueryService.getUserQueryByPublicId(id);
	}

}
