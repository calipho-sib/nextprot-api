package org.nextprot.api.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
@Api(name = "Protein lists", description = "Method to access queries without authentication (SPARQL)", group="Protein Lists")
public class PublicProteinListController {

	@Autowired
	private UserProteinListService proteinListService;

	// Collections /////////////////
	@ApiMethod(verb = ApiVerb.GET, description = "Gets all public lists", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/lists", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserProteinList> getLists() {
		return new ArrayList<UserProteinList>(); //empty for now
	}

	// Get on list /////////////////
	@ApiMethod(verb = ApiVerb.GET, description = "Get list by its public id", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/lists/{listId}", method = { RequestMethod.GET })
	@ResponseBody
	public UserProteinList getUserProteinList(@PathVariable("listId") String listId) {
		return proteinListService.getUserProteinListByPublicId(listId);
	}

}
