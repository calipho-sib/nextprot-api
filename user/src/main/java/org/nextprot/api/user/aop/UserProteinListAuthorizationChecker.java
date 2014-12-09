package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserProteinListAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserProteinListDao dao;

    @Override
    public void checkAuthorization(UserResource userProteinList) {

        String resourceOwner = userProteinList.getResourceOwner();

        if (userProteinList instanceof UserProteinList) {

            List<UserProteinList> foundUserProteinList = dao.getUserProteinLists(resourceOwner);

            if (!foundUserProteinList.isEmpty()) {

                String userProteinListOwner = foundUserProteinList.get(0).getResourceOwner();

                if (userProteinListOwner == null || !userProteinListOwner.equals(resourceOwner))
                    throw new NotAuthorizedException(resourceOwner + " cannot access resource");
            }
        } else {

            throw new IllegalStateException(userProteinList.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserProteinList;
    }
}
