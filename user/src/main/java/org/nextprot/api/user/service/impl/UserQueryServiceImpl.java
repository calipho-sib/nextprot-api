package org.nextprot.api.user.service.impl;

import org.apache.commons.lang.StringUtils;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.commons.utils.StringGenService;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.nextprot.api.user.utils.UserQueryUtils;
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
	private SparqlQueryDictionary sparqlQueryDictionary;

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
	public UserQuery getUserQueryById(long id) {
		UserQuery uq = getTutorialQueryById(id);
		if(uq != null) return uq;

		return userQueryDao.getUserQueryById(id);
	}

	@Override
	@AllowedAnonymous
	public UserQuery getUserQueryByPublicId(String id) 
	{
		UserQuery uq = null;
		if(StringUtils.isNumeric(id.replace("NXQ_", ""))){
			uq = getTutorialQueryById(Long.valueOf(id.replace("NXQ_", "")));
		}
		if(uq != null) return uq;

		return userQueryDao.getUserQueryByPublicId(id);
	}
	
	/**
	 * Retrieves from query sparql dictionary all the sparql queries tagged as "tutorial"
	 * Note
	 * Queries with tag "tutorial" are those visible in UI advanced queries (except those with tag "snorql-only")
	 * They are also visible in snorql query samples (including those with tag 'snorql-only)
	 */
	@Override
	@AllowedAnonymous
	@Cacheable(value = "nxq-tutorial-queries", sync = true)
	public List<UserQuery> getNxqTutorialQueries() {
		List<UserQuery> qlist = sparqlQueryDictionary.getSparqlQueryList();
		return UserQueryUtils.filterByTag(qlist, "tutorial");
	}

	/**
	 * Retrieves all the queries stored in the sparql query dictionary
	 * Note
	 * The list would not only contain tutorial queries but may include QC oriented queries, etc.
	 */
	@Override
	@AllowedAnonymous
	@Cacheable(value = "nxq-queries", sync = true)
	public List<UserQuery> getNxqQueries() {
		return sparqlQueryDictionary.getSparqlQueryList();
	}
	
	
	private UserQuery getTutorialQueryById(long id) {
		for(UserQuery uq : getNxqTutorialQueries()) { //TODO keep this on a map!!!!!!!!
			if(uq.getUserQueryId() == id){
				return uq;
			}
		}
		return null;
	}
}
