package org.nextprot.api.user.aop;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.commons.resource.UserResource;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.domain.User;
import org.nextprot.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect responsible for checking that services have permission to act on
 * {@code UserResource}
 *
 * @author fnikitin
 */
@Aspect
@Component
public class UserResourceAuthorizationAspect {

	private static final NotAuthorizedException NOT_AUTHORIZED_EXCEPTION = new NotAuthorizedException("You must be logged in to access this resource");

	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserDao userDao;

	@Around("execution(* org.nextprot.api.user.service.*.*(..)) && !@annotation(org.nextprot.api.commons.resource.AllowedAnonymous)")
	public Object checkUserAuthorization(ProceedingJoinPoint pjp) throws Throwable {

		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

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

			Object[] arguments = pjp.getArgs();
			for (Object arg : arguments) {
				if (arg instanceof UserResource) {

					UserResource untrustedUserResource = (UserResource) arg;

					untrustedUserResource.setOwnerName(usr.getUsername());
					untrustedUserResource.setOwnerId(usr.getId());

					// is the one that own the resource
					UserResourceAuthorizationChecker delegator = getAuthorizationChecker(untrustedUserResource);

					if (delegator == null) {

						throw new IllegalStateException("UserResourceAuthorizationChecker was not found for resource " + untrustedUserResource.getClass());
					}

					delegator.checkAuthorization(untrustedUserResource);

					NPSecurityContext.checkUserAuthorization(untrustedUserResource);

					break; // Do it only for the first argument

				}
			}

			//TODO test if the result is a collection of user resources....
			Object o = pjp.proceed();
			if (o instanceof UserResource) {
				UserResource untrustedUserResource = (UserResource) o;
				UserResourceAuthorizationChecker delegator = getAuthorizationChecker(untrustedUserResource);
				delegator.checkAuthorization(untrustedUserResource);
				NPSecurityContext.checkUserAuthorization(untrustedUserResource);
			}

			return o;


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