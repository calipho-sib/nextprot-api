package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Lazy
@Service
public class UserQueryServiceImpl implements UserQueryService {

	@Autowired
	private UserQueryDao userQueryDao;

	@Override
	public List<UserQuery> getUserQueries(String username) {
		return userQueryDao.getUserQueries(username);
	}

	@Override
	public List<UserQuery> getUserQueriesByTag(String tag) {
		return userQueryDao.getUserQueriesByTag(tag);
	}

	@Override
	@AllowedAnonymous
	public List<UserQuery> getPublishedQueries() {
		return userQueryDao.getPublishedQueries();
	}

	@Override
	public List<UserQuery> getTutorialQueries() {
		return userQueryDao.getTutorialQueries();
	}

	@Override
	@Transactional
	public UserQuery createUserQuery(UserQuery userQuery) {

		long id = userQueryDao.createUserQuery(userQuery);
		userQuery.setUserQueryId(id);
		if(userQuery.getTags() != null){
			userQueryDao.createUserQueryTags(id, userQuery.getTags());
		}
		return userQuery;
	}

	@Override
	public UserQuery updateUserQuery(UserQuery userQuery) {

		userQuery.checkValid();
		userQueryDao.updateUserQuery(userQuery);
		return userQuery;
	}

	@Override
	public void deleteUserQuery(UserQuery userQuery) {

		long queryId = userQuery.getUserQueryId();
		NPreconditions.checkNotNull(queryId, "Object not found");
		userQueryDao.deleteUserQuery(queryId);
	}

	@Override
	public UserQuery getUserQueryById(long id) {
		return userQueryDao.getUserQueryById(id);
	}
}
