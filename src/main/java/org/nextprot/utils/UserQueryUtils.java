package org.nextprot.utils;

import org.nextprot.api.domain.UserQuery;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserQueryUtils {

	public static boolean isAuthorized(UserQuery q) {

		String securityUserName = "";

		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null)
			return false;

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null)
			return false;

		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			securityUserName = currentUserDetails.getUsername();
		} else {
			securityUserName = a.getPrincipal().toString();
		}

		return (q.getUsername().equals(securityUserName));

	}
}
