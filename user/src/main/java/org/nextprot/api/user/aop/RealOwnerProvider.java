package org.nextprot.api.user.aop;

import org.nextprot.api.user.domain.UserResource;

/**
 * Provider responsible of fetching resource owner stored in nextprot-user db
 *
 * @author fnikitin
 */
public interface RealOwnerProvider {

    /**
     * Get owner of the resource stored in database
     * @param userResource the user resource
     * @return the owner name
     */
    String getRealOwner(UserResource userResource);

    /**
     * @return true if this provider supports this userResource
     */
    boolean supports(UserResource userResource);
}
