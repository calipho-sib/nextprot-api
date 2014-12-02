package org.nextprot.api.user.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.nextprot.api.commons.exception.NotAuthorizedException;
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

		String storedOwner = getStoredOwner(clientUserResource);

		if (storedOwner == null)
			throw new NotAuthorizedException(clientUserResource.getResourceOwner()+" cannot access resource");

		if (!storedOwner.equals(clientUserResource.getResourceOwner()))
			throw new NotAuthorizedException(clientUserResource.getResourceOwner()+" cannot access resource");
	}

	private String getStoredOwner(UserResource userResource) {

		Map<String, RealOwnerProvider> providers = context.getBeansOfType(RealOwnerProvider.class);

		for (RealOwnerProvider provider : providers.values()) {

			if (provider.supports(userResource)) {

				return provider.getRealOwner(userResource);
			}
		}

		return null;
	}
}