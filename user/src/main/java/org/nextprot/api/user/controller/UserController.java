package org.nextprot.api.user.controller;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthBasic;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.pojo.ApiVerb;
import org.nextprot.api.user.domain.User;
import org.nextprot.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * Controller for operating (CRUD) on user applications
 *
 * @author Daniel Teixeira
 */
@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@Api(name = "User", description = "Method to manipulate users.", group="User")
@ApiAuthBasic(roles={"ROLE_USER","ROLE_ADMIN"})
public class UserController {

    @Autowired
    private UserService userService;

   /* @ApiMethod(path = "/users", verb = ApiVerb.GET, description = "Gets all applications for a logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
    @RequestMapping(value = "/users", method = { RequestMethod.GET })
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<User> getApplications() {
        //return userService.getUserList();

        User user = new User();

        user.setId(23);
        user.setUsername("okkdoedko");

        return Arrays.asList(user);
    }*/

	
	/*@ApiMethod(path = "/user/applications", verb = ApiVerb.POST, description = "Creates a user application for the current logged user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/applications", method = { RequestMethod.POST })
	@ResponseBody
	public UserApplication createApplication(@RequestBody @ApiBodyObject UserApplication userApplication) {
		userApplication.setOwnerId(NPSecurityContext.getCurrentUserId());
		return userService.createUserApplication(userApplication);
	}
	
	@ApiMethod(path = "/user/applications/{id}", verb = ApiVerb.GET, description = "Gets the application of the current user", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/applications/{id}", method = { RequestMethod.GET })
	@ResponseBody
	public UserApplication getApplication(@PathVariable @ApiParam(name = "id", description = "The User application id", paramType=ApiParamType.PATH) Long id) {
		UserApplication userApp = userService.getUserApplication(id);
		NPSecurityContext.checkUserAuthorization(userApp);
		return userApp;
	}

	
	@ApiMethod(path = "/user/applications/{id}", verb = ApiVerb.DELETE, description = "Deletes an application", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = { MediaType.APPLICATION_JSON_VALUE})
	@RequestMapping(value = "/user/applications/{id}", method = { RequestMethod.DELETE })
	public void deleteApplication(@PathVariable @ApiParam(name = "id", description = "The User application id", paramType=ApiParamType.PATH) Long id) {
		UserApplication userApp = userService.getUserApplication(id);
		NPSecurityContext.checkUserAuthorization(userApp);
		userService.deleteApplication(id);
	}*/


}
