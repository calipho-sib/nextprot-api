package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.UserResource;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserApplicationAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserApplicationDao dao;

    @Override
    public void checkAuthorization(UserResource application) {

        long ownerId = application.getOwnerId();

        if (application instanceof UserApplication) {

            long appId = ((UserApplication) application).getId();

            // Checking authorization only done when application already exists
            if (application.isPersisted()) {

                UserApplication foundApp = dao.getUserApplicationById(appId);

                if (foundApp.getOwnerId() != ownerId)
                    throw new NotAuthorizedException(application.getOwnerName() + " cannot access resource");
            }
        } else {

            throw new IllegalStateException(application.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserApplication;
    }
}
