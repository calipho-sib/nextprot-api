package org.nextprot.api.user.aop;

import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserProteinListRealOwnerProvider implements RealOwnerProvider {

    @Autowired
    private UserProteinListDao userProteinListDao;

    @Override
    public String getRealOwner(UserResource userProteinList) {

        if (supports(userProteinList)) {

            String owner = ((UserProteinList) userProteinList).getOwner();

            List<UserProteinList> list = userProteinListDao.getUserProteinLists(owner);

            if (list.isEmpty())
                return null;

            return list.get(0).getResourceOwner();
        }

        return null;
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserProteinList;
    }
}
