package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.commons.utils.Base36Codec;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Lazy
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final Base36Codec.Generator generator = new Base36Codec.Generator();

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

        // TODO: check here that the random id is unique else generate another one
        String publicId = generator.nextBase36String();
        userQuery.setPublicId(publicId);

		long id = userQueryDao.createUserQuery(userQuery);

		userQuery.setUserQueryId(id);
		if (userQuery.getTags() != null) {
			userQueryDao.createUserQueryTags(id, userQuery.getTags());
		}
		return userQuery;
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
