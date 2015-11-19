package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@Api(name = "User Protein Lists", description = "Method to manipulate user protein lists", group="User")
@PreAuthorize("hasRole('ROLE_USER')")
@ApiAuthBasic(roles={"ROLE_USER"})
public class UserProteinListController {

	@Autowired
	private UserProteinListService proteinListService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	// List collection
	@ApiMethod(verb = ApiVerb.GET, description = "Gets user protein lists", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserProteinList> getUserProteinLists() {
		return this.proteinListService.getUserProteinLists(NPSecurityContext.getCurrentUser());
	}

	@ApiMethod(path = "/user/me/lists/{listId}", verb = ApiVerb.GET, description = "Gets user protein list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists/{listId}", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList getUserProteinList( @ApiPathParam(name = "listId", description = "The private id of the list", allowedvalues = { "" }) @PathVariable("listId") Integer listId) {
		return this.proteinListService.getUserProteinListById(listId);
	}

	// Create a list
	@ApiMethod(path = "/user/me/lists", verb = ApiVerb.POST, description = "Creates a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists", method = { RequestMethod.POST })
	@ResponseBody
	public UserProteinList createUserProteinList(@RequestBody UserProteinList proteinList) {

		Set<String> checkedAccessions = UserProteinListUtils.checkAndFormatAccessionNumbers(proteinList.getAccessionNumbers(),
				masterIdentifierService.findUniqueNames());

		proteinList.setAccessions(checkedAccessions);

		return this.proteinListService.createUserProteinList(proteinList);
	}

	// Update a list
	@ApiMethod(path = "/user/me/lists/{listid}", verb = ApiVerb.PUT, description = "Updates a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists/{listid}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserProteinList updateUserProteinList(@PathVariable("listid") String id, @RequestBody UserProteinList proteinList) {
		proteinList.setId(Long.parseLong(id));
		proteinListService.updateUserProteinList(proteinList);

		return proteinListService.getUserProteinListById(proteinList.getId());
	}

	//Delete a list
	@ApiMethod(verb = ApiVerb.DELETE, description = "Deletes a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/me/lists/{listid}", method = { RequestMethod.DELETE })
	public void deleteUserProteinList(@PathVariable("listid") String id) {
		UserProteinList userProteinList = proteinListService.getUserProteinListById(Long.parseLong(id));
		this.proteinListService.deleteUserProteinList(userProteinList);
	}
	
	//special operations on list
	@ApiMethod(verb = ApiVerb.GET, description = "Combines a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
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
	@RequestMapping(value = "/user/me/lists/{listid}/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void uploadProteinList(@RequestParam("file") MultipartFile file, @PathVariable(value = "listid") long listId, @RequestParam(value = "ignoreNotFoundEntries", defaultValue = "false") boolean ignoreNotFoundEntries) throws IOException {

		UserProteinList pl = proteinListService.getUserProteinListById(listId);

		Set<String> acs = UserProteinListUtils.parseAccessionNumbers(file, masterIdentifierService.findUniqueNames(), ignoreNotFoundEntries);
		pl.addAccessions(acs);
		
		this.proteinListService.updateUserProteinList(pl);
	}

}
