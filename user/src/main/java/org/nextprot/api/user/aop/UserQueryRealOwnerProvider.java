package org.nextprot.api.user.aop;

import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserQueryRealOwnerProvider implements RealOwnerProvider {

    @Autowired
    private UserQueryDao userQueryDao;

    @Override
    public String getRealOwner(UserResource query) {

        if (supports(query)) {

            long ownerId = ((UserQuery) query).getOwnerId();

            UserQuery uq = userQueryDao.getUserQueryById(ownerId);

            return uq.getResourceOwner();
        }

        return null;
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserQuery;
    }
}
