package org.nextprot.api.user.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserProteinListService.Operator;
import org.nextprot.api.user.service.UserService;
import org.nextprot.api.user.utils.UserProteinListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@Api(name = "User Lists", description = "Method to manipulate user lists", role = "ROLE_USER")
public class UserListController {

	private final Log Logger = LogFactory.getLog(UserListController.class);
	@Autowired
	private UserProteinListService proteinListService;

	@Autowired
	private MasterIdentifierService masterIdentifierService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserProteinList> getUserProteinLists(@PathVariable("username") String username) {
		return this.proteinListService.getUserProteinLists(username);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{name}", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList getList(@PathVariable("username") String username, @PathVariable("name") String listName, Model model) {
		return this.proteinListService.getUserProteinListByNameForUser(username, listName);
	}

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.POST })
	@ResponseBody
	public UserProteinList createList(@PathVariable("username") String username,
			@RequestBody UserProteinList proteinList, Model model) {
			return this.proteinListService.createUserProteinList(proteinList);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}", method = { RequestMethod.PUT })
	@ResponseBody
	public UserProteinList updateList(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserProteinList proteinList) {
		return this.proteinListService.updateUserProteinList(proteinList);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}", method = { RequestMethod.DELETE })
	public void deleteUserList(@PathVariable("username") String username, @PathVariable("id") String id) {

		UserProteinList userProteinList = proteinListService.getUserProteinListById(Long.parseLong(id));
		this.proteinListService.deleteUserProteinList(userProteinList);
	}


	@RequestMapping(value = "/user/{username}/protein-list/{list}/ids", method = RequestMethod.GET)
	@ResponseBody
	public Set<String> getListIds(@PathVariable("username") String username, @PathVariable("list") String listName, Model model) {
		UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		return proteinList.getAccessionNumbers();
	}

	@RequestMapping(value = "/user/{username}/protein-list/combine", method = RequestMethod.GET)
	@ResponseBody
	public UserProteinList combine(
			@PathVariable("username") String username, 
			@RequestParam(value = "name", required = true) String listName,
			@RequestParam(value = "description", required = false) String description, 
			@RequestParam(value = "first", required = true) String first,
			@RequestParam(value = "second", required = true) String second, 
			@RequestParam(value = "op", required = true) String operator) {

		UserProteinList combinedList = proteinListService.combine(listName, description, username, first, second, Operator.valueOf(operator));

		return proteinListService.createUserProteinList(combinedList);
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void upload(@RequestParam("file") MultipartFile file,
			@PathVariable("username") String username, 
			@PathVariable(value = "id") long listId) throws IOException {

		UserProteinList pl = proteinListService.getUserProteinListById(listId);
		Set<String> acs = UserProteinListUtils.parseAccessionNumbers(file, masterIdentifierService.findUniqueNames());
		pl.addAccessions(acs);
		
		this.proteinListService.updateUserProteinList(pl);

	}

}
