package org.nextprot.api.security.service.impl;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods related to the current logged in user
 * @author Daniel Teixeira
 *
 */
public class NPSecurityContext {

	/**
	 * Check authorization for all resources
	 * @param userResources
	 */
	public static void checkUserAuthorization(Collection<? extends UserResource> userResources) {
		for(UserResource resource : userResources){
			checkUserAuthorization(resource);
		}

	}

	public static void checkUserAuthorization(UserResource userResource) {

		String securityUserName;

		Authentication a = SecurityContextHolder.getContext().getAuthentication();

		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			securityUserName = currentUserDetails.getUsername();
		} else {
			securityUserName = a.getPrincipal().toString();
		}
		
		if(securityUserName == null){
			throw new NotAuthorizedException("Security user name not set!!!");
		}

		if (!securityUserName.equals(userResource.getOwnerName())) {
			throw new NotAuthorizedException(securityUserName + " is not authorized to access this resource");
		}

	}

	public static Set<String> getCurrentUserRoles() {

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null) {
			return new HashSet<String>(Arrays.asList("anonymous"));
		}

		Set<String> roles = new HashSet<String>();
		Collection<? extends GrantedAuthority> authorities = a.getAuthorities();
		for(GrantedAuthority auth : authorities){
			roles.add(auth.toString());
		}
		
		return roles;
	}

	public static String getCurrentUser() {

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			return currentUserDetails.getUsername();
		}else {
			return null;
		}
	}
}
