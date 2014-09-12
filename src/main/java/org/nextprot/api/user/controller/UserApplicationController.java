package org.nextprot.api.user.controller;

import java.util.List;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.security.NPSecurityContext;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for operating (CRUD) on user applications
 * 
 * @author Daniel Teixeira
 */
@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@Api(name = "User Application", description = "Method to manipulate applications. Applications are program that access the API", role="ROLE_USER")
public class UserApplicationController {

	@Autowired
	private UserApplicationService userApplicationService;

	@ApiMethod(path = "/user/applications", verb = ApiVerb.GET, description = "Gets all applications for a logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/applications", method = { RequestMethod.GET })
	@ResponseBody
	public List<UserApplication> getApplications() {
		return userApplicationService.getUserApplications(NPSecurityContext.getCurrentUser());
	}

	
	@ApiMethod(path = "/user/applications", verb = ApiVerb.POST, description = "Creates a user application for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/applications", method = { RequestMethod.POST })
	@ResponseBody
	public UserApplication createApplication(@RequestBody @ApiBodyObject UserApplication userApplication) {
		userApplication.setOwner(NPSecurityContext.getCurrentUser());
		return userApplicationService.createUserApplication(userApplication);
	}


}
