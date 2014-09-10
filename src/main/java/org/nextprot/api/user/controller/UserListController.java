package org.nextprot.api.user.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsondoc.core.annotation.Api;
import org.nextprot.api.user.domain.UserList;
import org.nextprot.api.user.service.UserListService;
import org.nextprot.api.user.service.UserListService.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Api(name = "User Lists", description = "Method to manipulate user lists", role = "ROLE_USER")
public class UserListController {
	
	private final Log Logger = LogFactory.getLog(UserListController.class);
	@Autowired
	private UserListService proteinListService;

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.GET })
	public String getLists(@PathVariable("username") String username, Model model) {

		// List<ProteinList> proteinLists = this.proteinListService.getProteinLists(username);
		List<UserList> proteinLists = this.proteinListService.getProteinListsMeta(username);
		model.addAttribute("proteinLists", proteinLists);
		return "protein-list-meta";
	}

	@RequestMapping(value = "/user/{username}/protein-list", method = { RequestMethod.POST })
	public String createList(@PathVariable("username") String username, @RequestBody UserList proteinList, Model model) {

		proteinList = this.proteinListService.createProteinList(proteinList);
		Logger.info("created list: " + proteinList.getId() + " > " + proteinList.getName());
		model.addAttribute("proteinList", proteinList);
		return "protein-list";

	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}", method = { RequestMethod.PUT })
	public Model updateList(@PathVariable("username") String username, @PathVariable("id") String id, @RequestBody UserList proteinList, Model model) {

		UserList updatedProteinList = this.proteinListService.updateProteinList(proteinList);
		model.addAttribute("proteinList", updatedProteinList);
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}", method = { RequestMethod.DELETE })
	public Model deleteList(@PathVariable("username") String username, @PathVariable("id") String id, Model model) {

		this.proteinListService.deleteProteinList(Long.parseLong(id));
		model.addAttribute("listId", id);
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}/add", method = { RequestMethod.PUT })
	public Model addElement(@PathVariable("username") String username, @PathVariable("id") String listName, @RequestBody List<String> accs, Model model) {
		UserList list = this.proteinListService.getProteinListByNameForUser(username, listName);
		this.proteinListService.addAccessions(list.getId(), new HashSet<String>(accs));

		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{id}/remove", method = { RequestMethod.PUT })
	public Model removeElement(@PathVariable("username") String username, @PathVariable("id") String listName, @RequestBody List<String> accs, Model model) {
		UserList list = this.proteinListService.getProteinListByNameForUser(username, listName);
		this.proteinListService.removeAccessions(list.getId(), new HashSet<String>(accs));
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/{name}", method = RequestMethod.GET)
	public String getList(@PathVariable("username") String username, @PathVariable("name") String listName, Model model) {

		UserList list = this.proteinListService.getProteinListByNameForUser(username, listName);
		model.addAttribute("list", list);
		return "protein-list";
	}

	@RequestMapping(value = "/user/{username}/protein-list/{list}/ids", method = RequestMethod.GET)
	public Model getIds(@PathVariable("username") String username, @PathVariable("list") String listName, Model model) {

		UserList proteinList = this.proteinListService.getProteinListByNameForUser(username, listName);
		model.addAttribute("ids", proteinList.getAccessions());
		return model;
	}

	@RequestMapping(value = "/user/{username}/protein-list/combine", method = RequestMethod.GET)
	public String combine(@PathVariable("username") String username, @RequestParam(value = "name", required = true) String listName,
			@RequestParam(value = "description", required = false) String description, @RequestParam(value = "first", required = true) String first,
			@RequestParam(value = "second", required = true) String second, @RequestParam(value = "op", required = true) String operation, Model model) {

		Operations op = Operations.valueOf(operation);

		if (op != null) {
			UserList list;
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
		UserList list = this.proteinListService.getProteinListById(listId);

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
		Set<String> currentAccs = list.getAccessions();

		for (String line : readLines) {
			trimmed = line.trim();
			if (line.charAt(0) != '#' && !currentAccs.contains(trimmed)) {
				accessions.add(trimmed);
			}
		}
		this.proteinListService.addAccessions(listId, accessions);
	}

}
