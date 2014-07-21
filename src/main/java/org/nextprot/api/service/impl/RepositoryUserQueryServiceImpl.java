package org.nextprot.api.service.impl;

import java.util.List;

import org.nextprot.api.dao.RepositoryUserQueryDao;
import org.nextprot.api.domain.UserQuery;
import org.nextprot.api.service.RepositoryUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class RepositoryUserQueryServiceImpl implements RepositoryUserQueryService {

	@Autowired
	private RepositoryUserQueryDao userQueryDao;

	@Override
	public List<UserQuery> getUserQueries(String username) {
		return userQueryDao.getUserQueries(username);
	}

	@Override
	public List<UserQuery> getPublicQueries() {
		return userQueryDao.getPublicQueries();
	}

	@Override
	@PreAuthorize("{T(org.nextprot.utils.UserQueryUtils).isAuthorized(#userQuery)}")
	public UserQuery createUserQuery(UserQuery userQuery) {
		userQuery.checkValid();
		long id = userQueryDao.saveUserQuery(userQuery);
		userQuery.setUserQueryId(id);
		return userQuery;
	}

	@Override
	@PreAuthorize("{T(org.nextprot.utils.UserQueryUtils).isAuthorized(#userQuery)}")
	public UserQuery updateUserQuery(UserQuery userQuery) {
		userQuery.checkValid();
		userQueryDao.updateUserQuery(userQuery);
		return userQuery;
	}

	@Override
	@PreAuthorize("{T(org.nextprot.utils.UserQueryUtils).isAuthorized(#userQuery)}")
	public void deleteUserQuery(UserQuery userQuery) {
		userQueryDao.deleteUserQuery(userQuery.getUserQueryId());
	}

	@Override
	public List<UserQuery> getNextprotQueries() {
		return userQueryDao.getNextprotQueries();
	}

	@Override
	public UserQuery getUserQueryById(long id) {
		return userQueryDao.getUserQueryById(id);
	}
}
