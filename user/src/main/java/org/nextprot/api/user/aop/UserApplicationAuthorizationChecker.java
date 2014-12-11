package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserApplicationAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserApplicationDao dao;

    @Override
    public void checkAuthorization(UserResource application) {

        String resourceOwner = application.getResourceOwner();

        if (application instanceof UserApplication) {

            long appId = ((UserApplication) application).getId();

            // Checking authorization only done when application already exists
            if (appId > 0) {

                UserApplication foundApp = dao.getUserApplicationById(appId);

                boolean authorizationPassed = false;

                if (foundApp != null) {

                    String foundOwner = foundApp.getResourceOwner();

                    authorizationPassed = foundOwner != null && foundOwner.equals(resourceOwner);
                }

                if (!authorizationPassed) throw new NotAuthorizedException(resourceOwner + " cannot access resource");
            }
        } else {

            throw new IllegalStateException(resourceOwner.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserApplication;
    }
}
