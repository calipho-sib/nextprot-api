package org.nextprot.api.core.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.nextprot.auth.core.domain.NextprotUser;
import org.nextprot.auth.core.exception.NextprotUserException;
import org.nextprot.auth.core.service.NextprotUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Lazy
@Controller
public class UserController {
//	@Autowired private UserService userService;
	@Autowired private NextprotUserService userService;
	
	
//	@RequestMapping(value="/user/{id}", method = RequestMethod.GET)
//	public String getUser(@PathVariable("id") String userId, Model model) {
//		User user = this.userService.getUserById(Long.parseLong(userId));
//		model.addAttribute("user", user);
//		return "user";
//	}
	
	@RequestMapping(value="/user/{username}", method = RequestMethod.GET)
	public String getUserByUsername(@PathVariable("username") String username, Model model) {
		
		NextprotUser user = this.userService.getUserByUsername(username);
		model.addAttribute("user", user);
		return "user";
	}
	
	
	@RequestMapping(value="/user/me", method = RequestMethod.GET)
	public String me(HttpServletRequest request, Model model) {
		
		Principal principal = request.getUserPrincipal();
		
		if(principal != null) {// logged in user
			NextprotUser user = this.userService.getUserByUsername(principal.getName());
			model.addAttribute("user", user);
			return "user";
		}
		
		return null;
	}
	
	
	@RequestMapping(value="/user", method = RequestMethod.GET)
	public String getUsers() {
		return "users/show";
	}
	
	
	
//	Exception Handling
	
	@ExceptionHandler(NextprotUserException.class)
	public ResponseEntity<String> userNotFound(NextprotUserException exception) {
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
	}
	
}
