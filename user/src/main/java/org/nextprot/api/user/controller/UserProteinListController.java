package org.nextprot.api.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserProteinListService.Operator;
import org.nextprot.api.user.utils.UserProteinListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Api(name = "User Protein Lists", description = "Method to manipulate user protein lists", group="User")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserProteinListController {

	@Autowired
	private UserProteinListService proteinListService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	// List collection
	@ApiMethod(verb = ApiVerb.GET, description = "Gets user protein lists", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists", method = { RequestMethod.GET })
	@ResponseBody
	@PreAuthorize("hasRole('ROLE_USER')")
	public List<UserProteinList> getUserProteinLists() {
		return this.proteinListService.getUserProteinLists(NPSecurityContext.getCurrentUser());
	}

	@ApiMethod(path = "/user/me/lists/{listId}", verb = ApiVerb.GET, description = "Gets user protein list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists/{listId}", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList getUserProteinList(@PathVariable("listId") Integer listId) {
		return this.proteinListService.getUserProteinListById(listId);
	}

	// Create a list
	@ApiMethod(path = "/user/me/lists", verb = ApiVerb.POST, description = "Creates a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/me/lists", method = { RequestMethod.POST })
	@ResponseBody
	public UserProteinList createUserProteinList(@RequestBody UserProteinList proteinList) {
		return this.proteinListService.createUserProteinList(proteinList);
	}

	// Update a list
	@ApiMethod(path = "/user/me/lists/{listid}", verb = ApiVerb.PUT, description = "Updates a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/me/lists/{listid}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserProteinList updateUserProteinList(@PathVariable("listid") String id, @RequestBody UserProteinList proteinList) {
		proteinList.setId(Long.parseLong(id));
		return this.proteinListService.updateUserProteinList(proteinList);
	}

	//Delete a list
	@ApiMethod(verb = ApiVerb.DELETE, description = "Deletes a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/me/lists/{listid}", method = { RequestMethod.DELETE })
	public void deleteUserProteinList(@PathVariable("listid") String id) {
		UserProteinList userProteinList = proteinListService.getUserProteinListById(Long.parseLong(id));
		this.proteinListService.deleteUserProteinList(userProteinList);
	}
	
	//special operations on list
	@ApiMethod(verb = ApiVerb.GET, description = "Combines a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/me/lists/combine", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList combineUserProteinList( 
			@RequestParam(value = "listname", required = true) String listName,
			@RequestParam(value = "description", required = false) String description, 
			@RequestParam(value = "listname1", required = true) String listname1,
			@RequestParam(value = "listname2", required = true) String listname2,
			@RequestParam(value = "op", required = true) String operator) {

		UserProteinList combinedList = proteinListService.combine(listName, description, NPSecurityContext.getCurrentUser(), listname1, listname2,
				Operator.valueOf(operator));

		return proteinListService.createUserProteinList(combinedList);
	}

	@ApiMethod(path = "/user/me/lists/{listid}/upload", verb = ApiVerb.POST, description = "Uploads a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/me/lists/{listid}/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void uploadProteinList(@RequestParam("file") MultipartFile file, @PathVariable(value = "listid") long listId) throws IOException {

		UserProteinList pl = proteinListService.getUserProteinListById(listId);
		Set<String> acs = UserProteinListUtils.parseAccessionNumbers(file, masterIdentifierService.findUniqueNames());
		pl.addAccessions(acs);
		
		this.proteinListService.updateUserProteinList(pl);
	}

}
