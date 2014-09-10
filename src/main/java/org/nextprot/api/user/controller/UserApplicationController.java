package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiBodyObject;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiParam;
import org.jsondoc.core.pojo.ApiParamType;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.user.domain.UserTest;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for operating (CRUD) on user applications (SPARQL)
 * 
 * @author Daniel Teixeira
 */
@Lazy
@Controller
@Api(name = "User Application", description = "Method to manipulate applications. Applications are program that access the API", role="ROLE_USER")
public class UserApplicationController {

	@Autowired
	private UserApplicationService userApplicationService;

	@ApiMethod(path = "/user/{username}/applications", verb = ApiVerb.POST, description = "Creates an application", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/{username}/applications", method = { RequestMethod.POST })
	@ResponseBody
	public UserTest createUserApplication(@RequestBody @ApiBodyObject UserTest application, @ApiParam(name = "username", description = "The username", paramType=ApiParamType.PATH) @PathVariable("username") String username) {
		UserTest u = new UserTest();
		u.setName("lol");
		return u;
		//.createUserApplication(application);
	}

}
