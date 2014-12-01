package org.nextprot.api.user.service.impl;

import java.util.List;

import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public List<UserQuery> getPublishedQueries() {
		return userQueryDao.getPublishedQueries();
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
		NPSecurityContext.checkUserAuthorization(userQuery);
		userQuery.checkValid();
		userQueryDao.updateUserQuery(userQuery);
		return userQuery;
	}

	@Override
	public void deleteUserQuery(UserQuery userQuery) {
		NPSecurityContext.checkUserAuthorization(userQuery);
		userQueryDao.deleteUserQuery(userQuery.getUserQueryId());
	}

	@Override
	public UserQuery getUserQueryById(long id) {
		return userQueryDao.getUserQueryById(id);
	}
}
