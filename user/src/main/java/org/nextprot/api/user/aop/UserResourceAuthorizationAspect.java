package org.nextprot.api.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nextprot.api.user.domain.UserResource;
import org.nextprot.api.user.security.NPSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Aspect responsible to check service users authorizing to access resources
 *
 * @author fnikitin
 */
@Aspect
public class UserResourceAuthorizationAspect {

	@Autowired
	private ApplicationContext context;

 	@Before("execution(* org.nextprot.api.user.service.*.*(..)) && args(clientUserResource)")
	public void checkAuthorization(UserResource clientUserResource) throws Throwable {

		NPSecurityContext.checkUserAuthorization(clientUserResource);

		UserResourceAuthorizationChecker checker = getAuthorizationChecker(clientUserResource);

		checker.checkAuthorization(clientUserResource);
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