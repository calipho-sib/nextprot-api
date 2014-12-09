package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserQueryUserResourceAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserQueryDao dao;

    @Override
    public void checkAuthorization(UserResource query) {

        String resourceOwner = query.getResourceOwner();

        if (query instanceof UserQuery) {

            long ownerId = ((UserQuery) query).getOwnerId();

            UserQuery foundUserQuery = dao.getUserQueryById(ownerId);

            if (foundUserQuery == null || foundUserQuery.getResourceOwner() == null || !foundUserQuery.getResourceOwner().equals(resourceOwner))
                throw new NotAuthorizedException(resourceOwner + " cannot access resource");
        } else {

            throw new IllegalStateException(query.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserQuery;
    }
}
