package org.nextprot.api.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nextprot.api.user.domain.UserResource;
import org.nextprot.api.user.security.NPSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Aspect responsible for checking that services have permission to act on {@code UserResource}
 *
 * @author fnikitin
 */
@Aspect
public class UserResourceAuthorizationAspect {

	@Autowired
	private ApplicationContext context;

 	@Before("execution(* org.nextprot.api.user.service.*.*(..)) && args(clientUserResource)")
	public void checkAuthorization(UserResource clientUserResource) throws Throwable {

		// check the logged-in client
		NPSecurityContext.checkUserAuthorization(clientUserResource);

		// is the one that own the resource
		UserResourceAuthorizationChecker delegator = getAuthorizationChecker(clientUserResource);

		delegator.checkAuthorization(clientUserResource);
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