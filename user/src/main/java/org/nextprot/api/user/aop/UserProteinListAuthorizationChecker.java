package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.UserResource;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserProteinListAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserProteinListDao dao;

    @Override
    public void checkAuthorization(UserResource userProteinList) {

        long ownerId = userProteinList.getOwnerId();

        if (userProteinList instanceof UserProteinList) {


            // Checking authorization only done when application already exists
            if (userProteinList.isPersisted()) {

                UserProteinList foundUserProteinList = dao.getUserProteinListById(((UserProteinList) userProteinList).getId());

                if (foundUserProteinList.getOwnerId() != ownerId)
                    throw new NotAuthorizedException(foundUserProteinList.getOwnerName() + " cannot access resource");

            }

        }
        else {

            throw new IllegalStateException(userProteinList.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserProteinList;
    }
}
