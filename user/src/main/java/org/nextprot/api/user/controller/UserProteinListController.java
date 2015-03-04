package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.commons.service.MasterIdentifierService;
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
public class UserProteinListController {

	@Autowired
	private UserProteinListService proteinListService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@ApiMethod(path = "/user/{username}/protein-list", verb = ApiVerb.GET, description = "Gets user protein lists", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserProteinList> getUserProteinLists(@PathVariable("username") String username) {
		return this.proteinListService.getUserProteinLists(username);
	}

	@ApiMethod(path = "/user/{username}/protein-list/{listname}", verb = ApiVerb.GET, description = "Gets user protein list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/{username}/protein-list/{listname}", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList getUserProteinList(@PathVariable("username") String username, @PathVariable("listname") String listName) {
		return this.proteinListService.getUserProteinListByNameForUser(username, listName);
	}

	@ApiMethod(path = "/user/{username}/protein-list/{listname}/accnums", verb = ApiVerb.GET, description = "Gets user protein list accession numbers", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/{username}/protein-list/{listname}/accnums", method = RequestMethod.GET)
	@ResponseBody
	public Set<String> getUserProteinListAccessionNumbers(@PathVariable("username") String username, @PathVariable("listname") String listName) {
		UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		return proteinList.getAccessionNumbers();
	}

	@ApiMethod(path = "/user/{username}/protein-list", verb = ApiVerb.POST, description = "Creates a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.POST })
	@ResponseBody
	public UserProteinList createUserProteinList(@PathVariable("username") String username, @RequestBody UserProteinList proteinList) {

		proteinList.setOwner(username);
		return this.proteinListService.createUserProteinList(proteinList);
	}

	@ApiMethod(path = "/user/{username}/protein-list/{listid}", verb = ApiVerb.PUT, description = "Updates a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/protein-list/{listid}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserProteinList updateUserProteinList(@PathVariable("username") String username, @PathVariable("listid") String id,
												 @RequestBody UserProteinList proteinList) {

		proteinList.setId(Long.parseLong(id));

		return this.proteinListService.updateUserProteinList(proteinList);
	}

	@ApiMethod(path = "/user/{username}/protein-list/{listid}", verb = ApiVerb.DELETE, description = "Deletes a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/protein-list/{listid}", method = { RequestMethod.DELETE })
	public void deleteUserProteinList(@PathVariable("username") String username, @PathVariable("listid") String id) {

		UserProteinList userProteinList = proteinListService.getUserProteinListById(Long.parseLong(id));
		this.proteinListService.deleteUserProteinList(userProteinList);
	}

	@ApiMethod(path = "/user/{username}/protein-list/combine", verb = ApiVerb.GET, description = "Combines a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/protein-list/combine", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList combineUserProteinList(
			@PathVariable("username") String username, 
			@RequestParam(value = "listname", required = true) String listName,
			@RequestParam(value = "description", required = false) String description, 
			@RequestParam(value = "listname1", required = true) String listname1,
			@RequestParam(value = "listname2", required = true) String listname2,
			@RequestParam(value = "op", required = true) String operator) {

		UserProteinList combinedList = proteinListService.combine(listName, description, username, listname1, listname2,
				Operator.valueOf(operator));

		return proteinListService.createUserProteinList(combinedList);
	}

	@ApiMethod(path = "/user/{username}/protein-list/{listid}/upload", verb = ApiVerb.POST, description = "Uploads a user protein list for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/{username}/protein-list/{listid}/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void uploadProteinList(@RequestParam("file") MultipartFile file,
			@PathVariable("username") String username, 
			@PathVariable(value = "listid") long listId) throws IOException {

		UserProteinList pl = proteinListService.getUserProteinListById(listId);
		Set<String> acs = UserProteinListUtils.parseAccessionNumbers(file, masterIdentifierService.findUniqueNames());
		pl.addAccessions(acs);
		
		this.proteinListService.updateUserProteinList(pl);
	}
}
