package org.nextprot.api.user.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService;
import org.nextprot.api.user.service.UserProteinListService.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@Api(name = "User Lists", description = "Method to manipulate user lists", role = "ROLE_USER")
public class UserListController {
	
	private final Log Logger = LogFactory.getLog(UserListController.class);
	@Autowired
	private UserProteinListService proteinListService;

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.GET })
	public String getLists(@PathVariable("username") String username, Model model) {

		// List<ProteinList> proteinLists = this.proteinListService.getProteinLists(username);
		List<UserProteinList> proteinLists = this.proteinListService.getUserProteinLists(username);
		model.addAttribute("proteinLists", proteinLists);
		return "protein-list-meta";
	}

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.POST })
	public String createList(@PathVariable("username") String username, @RequestBody UserProteinList proteinList, Model model) {

		proteinList = this.proteinListService.createUserProteinList(proteinList);
		Logger.info("created list: " + proteinList.getId() + " > " + proteinList.getName());
		model.addAttribute("proteinList", proteinList);
		return "protein-list";

	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}", method = { RequestMethod.PUT })
	public Model updateList(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserProteinList proteinList, Model model) {

		UserProteinList updatedProteinList = this.proteinListService.updateUserProteinList(proteinList);
		model.addAttribute("proteinList", updatedProteinList);
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}", method = { RequestMethod.DELETE })
	public Model deleteList(@PathVariable("username") String username, @PathVariable("id") String id, Model model) {

		this.proteinListService.deleteUserProteinList(Long.parseLong(id));
		model.addAttribute("listId", id);
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}/add", method = { RequestMethod.PUT })
	public Model addElement(@PathVariable("username") String username, @PathVariable("id") String listName, @RequestBody List<String> accs, Model model) {
		UserProteinList list = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		this.proteinListService.addAccessionNumbers(list.getId(), new HashSet<String>(accs));

		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}/remove", method = { RequestMethod.PUT })
	public Model removeElement(@PathVariable("username") String username, @PathVariable("id") String listName, @RequestBody List<String> accs, Model model) {
		UserProteinList list = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		this.proteinListService.removeAccessionNumbers(list.getId(), new HashSet<String>(accs));
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{name}", method = RequestMethod.GET)
	public String getList(@PathVariable("username") String username, @PathVariable("name") String listName, Model model) {

		UserProteinList list = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		model.addAttribute("list", list);
		return "protein-list";
	}

	@RequestMapping(value = "/user/{username}/protein-list/{list}/ids", method = RequestMethod.GET)
	public Model getIds(@PathVariable("username") String username, @PathVariable("list") String listName, Model model) {

		UserProteinList proteinList = this.proteinListService.getUserProteinListByNameForUser(username, listName);
		model.addAttribute("ids", proteinList.getAccessionNumbers());
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/combine", method = RequestMethod.GET)
	public String combine(@PathVariable("username") String username, @RequestParam(value = "name", required = true) String listName,
			@RequestParam(value = "description", required = false) String description, @RequestParam(value = "first", required = true) String first,
			@RequestParam(value = "second", required = true) String second, @RequestParam(value = "op", required = true) String operation, Model model) {

		Operations op = Operations.valueOf(operation);

		if (op != null) {
			UserProteinList list;
			list = proteinListService.combine(listName, description, username, first, second, Operations.valueOf(operation));
			model.addAttribute("proteinList", list);
			return "protein-list";

		} else {
			model.addAttribute("errormessage", "Invalid operation");
			return "exception";
		}
	}

	@RequestMapping(value = "/protein-list/upload", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void upload(@RequestParam("file") MultipartFile file, @RequestParam(value = "id", required = true) String id, String locid) throws IOException {

		long listId = Long.parseLong(id);
		UserProteinList list = this.proteinListService.getUserProteinListById(listId);

		InputStream inputStream = file.getInputStream();

		StringBuilder stringBuilder = new StringBuilder();

		if (file.getInputStream() != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			char[] charBuffer = new char[128];
			int bytesRead = -1;

			while ((bytesRead = reader.read(charBuffer)) > 0) {
				stringBuilder.append(charBuffer, 0, bytesRead);
			}
		} else {
			stringBuilder.append("");
		}

		String[] readLines = stringBuilder.toString().split("\n");

		String trimmed;
		Set<String> accessions = new HashSet<String>();
		Set<String> currentAccs = list.getAccessionNumbers();

		for (String line : readLines) {
			trimmed = line.trim();
			if (line.charAt(0) != '#' && !currentAccs.contains(trimmed)) {
				accessions.add(trimmed);
			}
		}
		this.proteinListService.addAccessionNumbers(listId, accessions);
	}

}
