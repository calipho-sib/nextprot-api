package org.nextprot.api.user.security;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class NPContext {

	public static void isUserAuthorized(UserResource userResource){

		String securityUserName = "";
		
		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null){
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null){
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}
		
		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			securityUserName = currentUserDetails.getUsername();
		} else {
			securityUserName = a.getPrincipal().toString();
		}
		
		if (!userResource.getResourceOwner().equals(securityUserName)) {
			throw new NotAuthorizedException(securityUserName + " is not authorized to modify this resource");
		}

	}
}
