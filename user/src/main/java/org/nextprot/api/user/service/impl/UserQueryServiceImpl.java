package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.commons.utils.StringGenService;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Lazy
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private static final String PUBLIC_ID_UNIQUE_CONSTRAINT_NAME = "user_queries_pubid_udx";

    @Autowired
    private StringGenService generator;

	@Autowired
	private UserQueryDao userQueryDao;

	@Autowired
	private UserQueryTutorialDictionary userQueryTutorialDictionary;

	@Override
	@Cacheable(value = "user-queries", key = "#username")
	public List<UserQuery> getUserQueries(String username) {
		return userQueryDao.getUserQueries(username);
	}

	@Override
	public List<UserQuery> getUserQueriesByTag(String tag) {
		return userQueryDao.getUserQueriesByTag(tag);
	}

	@Override
	@Transactional
	@CacheEvict(value = "user-queries", key = "#userQuery.getOwner()")
	public UserQuery createUserQuery(UserQuery userQuery) {

        NPreconditions.checkNotNull(userQuery, "The user query should not be null");

        generatePubidAndCreate(userQuery);

        if (userQuery.getTags() != null)
            userQueryDao.createUserQueryTags(userQuery.getUserQueryId(), userQuery.getTags());

		return userQuery;
	}

    /**
     * Generate and set public id into userQuery then dao createUserQuery
     * @param userQuery
     *
     * @throws DuplicateKeyException if
     */
    private void generatePubidAndCreate(UserQuery userQuery) {

        int maxLoop = 10;

        DuplicateKeyException e;

        int count=0;
        do {
            userQuery.setPublicId(generator.generateString());

            try {
                long id = userQueryDao.createUserQuery(userQuery);
                userQuery.setUserQueryId(id);
                e = null;

            } catch (DuplicateKeyException dke) {

                if (!dke.getMessage().contains(PUBLIC_ID_UNIQUE_CONSTRAINT_NAME) || count >= maxLoop)
                    throw dke;

                e = dke;
            }
            count++;
        } while(e != null);
    }

	@Override
	@CacheEvict(value = "user-queries", key = "#userQuery.getOwner()")
	public UserQuery updateUserQuery(UserQuery userQuery) {

		userQuery.checkValidForUpdate();
		userQueryDao.updateUserQuery(userQuery);
		return userQuery;
	}

	@Override
	@CacheEvict(value = "user-queries", key = "#userQuery.getOwner()")
	public void deleteUserQuery(UserQuery userQuery) {

		long queryId = userQuery.getUserQueryId();
		NPreconditions.checkNotNull(queryId, "Object not found");
		userQueryDao.deleteUserQuery(queryId);
	}

	@Override
	@AllowedAnonymous
	public UserQuery getUserQueryById(long id) {
		for(UserQuery uq : getTutorialQueries()) { //TODO keep this on a map!!!!!!!!
			if(uq.getUserQueryId() == id){
				return uq;
			}
		}
		return userQueryDao.getUserQueryById(id);
	}

	@Override
	@AllowedAnonymous
	@Cacheable("tutorial-queries")
	public List<UserQuery> getTutorialQueries() {
		return userQueryTutorialDictionary.getDemoSparqlList();
	}
}
