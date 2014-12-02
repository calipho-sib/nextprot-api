package org.nextprot.api.user.aop;

import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserApplicationRealOwnerProvider implements RealOwnerProvider {

    @Autowired
    private UserApplicationDao userApplicationDao;

    @Override
    public String getRealOwner(UserResource application) {

        if (supports(application)) {

            long ownerId = ((UserApplication) application).getOwnerId();

            List<UserApplication> apps = userApplicationDao.getUserApplicationListByOwnerId(ownerId);

            if (apps.isEmpty())
                return null;

            return apps.get(0).getResourceOwner();
        }

        return null;
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserApplication;
    }
}
