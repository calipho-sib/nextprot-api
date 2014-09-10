package org.nextprot.api.user.service.impl;

import java.util.List;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.domain.UserResource;
import org.nextprot.api.user.security.NPSecurityContext;
import org.nextprot.api.user.service.UserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
	public List<UserQuery> getPublicQueries() {
		return userQueryDao.getPublicQueries();
	}

	@Override
	public UserQuery createUserQuery(UserQuery userQuery) {
		NPSecurityContext.checkUserAuthorization(userQuery);
		userQuery.checkValid();
		long id = userQueryDao.saveUserQuery(userQuery);
		userQuery.setUserQueryId(id);
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
	public List<UserQuery> getNextprotQueries() {
		return userQueryDao.getNextprotQueries();
	}

	@Override
	public UserQuery getUserQueryById(long id) {
		return userQueryDao.getUserQueryById(id);
	}
	
	

}
