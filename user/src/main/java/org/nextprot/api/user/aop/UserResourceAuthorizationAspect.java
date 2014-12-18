package org.nextprot.api.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.UserResource;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Aspect responsible for checking that services have permission to act on {@code UserResource}
 *
 * @author fnikitin
 */
@Aspect
@Component
public class UserResourceAuthorizationAspect {

	private static final NotAuthorizedException NOT_AUTHORIZED_EXCEPTION =
			new NotAuthorizedException("You must be logged in to access this resource");

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserDao userDao;

 	@Before("execution(* org.nextprot.api.user.service.*.*(..)) && args(untrustedUserResource)")
	public void checkAuthorization(UserResource untrustedUserResource) throws Throwable {

		// check that client is logged in
		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null || sc.getAuthentication() == null) {
			throw NOT_AUTHORIZED_EXCEPTION;
		}

		// set resource owner with the connected user
		String currentUser = NPSecurityContext.getCurrentUser();
		if (currentUser == null) {
			throw NOT_AUTHORIZED_EXCEPTION;
		}

		User usr = userDao.getUserByUsername(currentUser);

		untrustedUserResource.setOwnerName(usr.getUsername());
		untrustedUserResource.setOwnerId(usr.getId());

		// is the one that own the resource
		UserResourceAuthorizationChecker delegator = getAuthorizationChecker(untrustedUserResource);

		if (delegator == null) {

			throw new IllegalStateException("UserResourceAuthorizationChecker was not found for resource "+untrustedUserResource.getClass());
		}

		delegator.checkAuthorization(untrustedUserResource);

		NPSecurityContext.checkUserAuthorization(untrustedUserResource);
	}

	private UserResourceAuthorizationChecker getAuthorizationChecker(UserResource userResource) {

		Map<String, UserResourceAuthorizationChecker> checkers = context.getBeansOfType(UserResourceAuthorizationChecker.class);

		for (UserResourceAuthorizationChecker checker : checkers.values()) {

			if (checker.supports(userResource)) {

				return checker;
			}
		}

		return null;
	}
}