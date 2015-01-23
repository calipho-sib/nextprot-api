package org.nextprot.api.user.aop;

import org.nextprot.api.commons.resource.UserResource;

/**
 * Check that nextprot-user resource can be accessed
 *
 * @author fnikitin
 */
public interface UserResourceAuthorizationChecker {

    /**
     * Test that userResource has valid permission
     * @param userResource the user resource to test authorization on
     * @throw NotAuthorizedException if not
     */
    void checkAuthorization(UserResource userResource);

    /**
     * @return true if checker can checks userResource
     */
    boolean supports(UserResource userResource);
}
