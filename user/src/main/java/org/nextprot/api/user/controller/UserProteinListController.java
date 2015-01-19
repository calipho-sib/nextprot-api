package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserProteinListService.Operator;
import org.nextprot.api.user.utils.UserProteinListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@Api(name = "User Protein Lists", description = "Method to manipulate user protein lists", role = "ROLE_USER")
public class UserProteinListController {

	@Autowired
	private UserProteinListService proteinListService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserProteinList> getUserProteinLists(@PathVariable("username") String username) {
		return this.proteinListService.getUserProteinLists(username);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{listname}", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList getUserProteinList(@PathVariable("username") String username, @PathVariable("listname") String listName) {
		return this.proteinListService.getUserProteinListByNameForUser(username, listName);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{listname}/accnums", method = RequestMethod.GET)
	@ResponseBody
	public Set<String> getUserProteinListAccessionNumbers(@PathVariable("username") String username, @PathVariable("listname") String listName) {
		UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		return proteinList.getAccessionNumbers();
	}

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.POST })
	@ResponseBody
	public UserProteinList createUserProteinList(@PathVariable("username") String username, @RequestBody UserProteinList proteinList) {

		proteinList.setOwner(username);
		return this.proteinListService.createUserProteinList(proteinList);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{listid}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserProteinList updateUserProteinList(@PathVariable("username") String username, @PathVariable("listid") String id,
												 @RequestBody UserProteinList proteinList) {

		proteinList.setId(Long.parseLong(id));

		return this.proteinListService.updateUserProteinList(proteinList);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{listid}", method = { RequestMethod.DELETE })
	public void deleteUserProteinList(@PathVariable("username") String username, @PathVariable("listid") String id) {

		UserProteinList userProteinList = proteinListService.getUserProteinListById(Long.parseLong(id));
		this.proteinListService.deleteUserProteinList(userProteinList);
	}

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
