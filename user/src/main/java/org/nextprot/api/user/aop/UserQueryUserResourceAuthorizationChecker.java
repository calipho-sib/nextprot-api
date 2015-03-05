package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.UserResource;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserQueryUserResourceAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserQueryDao dao;

    @Override //THIS SHOULD ONLY BE INTERCEPT FOR DELETES INSERT AND UPDATED
    public void checkAuthorization(UserResource query) {

        if (query instanceof UserQuery) {

        	long queryId = ((UserQuery) query).getUserQueryId();
            if (queryId != 0){ 

            	if((queryId < 100000)){ //Tutorial query
            		throw new NotAuthorizedException("Tutorial queries can't be modified");
            	}
            	
            	UserQuery foundUserQuery = dao.getUserQueryById(queryId);

                // dao only get owner name
                if (!foundUserQuery.getOwner().equals(query.getOwnerName()))
                    throw new NotAuthorizedException(query.getOwnerName() + " cannot access resource");
            }
        }
        else {

            throw new IllegalStateException(query.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserQuery;
    }
}
