package org.nextprot.api.user.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import sib.calipho.spring.security.auth0.Auth0UserDetails;

/**
 * Utility methods related to the current logged in user
 * @author Daniel Teixeira
 *
 */
public class NPSecurityContext {

	public static void checkUserAuthorization(UserResource userResource) {

		String securityUserName = "";

		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null) {
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null) {
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

	public static Set<String> getCurrentUserRoles() {

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null) {
			return new HashSet<String>(Arrays.asList("anonymous"));
		}

		Set<String> roles = new HashSet<String>();
		if (a.getPrincipal() instanceof Auth0UserDetails) {
			Auth0UserDetails currentUserDetails = (Auth0UserDetails) a.getPrincipal();
			Collection<? extends GrantedAuthority> authorities = currentUserDetails.getAuthorities();
			for(GrantedAuthority auth : authorities){
				roles.add(auth.toString());
			}
		}
		
		return roles;
	}
}
