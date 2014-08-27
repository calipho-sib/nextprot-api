package org.nextprot.api.core.service.impl;

import java.util.List;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.core.dao.RepositoryUserQueryDao;
import org.nextprot.api.core.domain.UserQuery;
import org.nextprot.api.core.service.RepositoryUserQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
	public UserQuery createUserQuery(UserQuery userQuery) {
		checkIsAuthorized(userQuery);
		userQuery.checkValid();
		long id = userQueryDao.saveUserQuery(userQuery);
		userQuery.setUserQueryId(id);
		return userQuery;
	}

	@Override
	public UserQuery updateUserQuery(UserQuery userQuery) {
		checkIsAuthorized(userQuery);
		userQuery.checkValid();
		userQueryDao.updateUserQuery(userQuery);
		return userQuery;
	}

	@Override
	public void deleteUserQuery(UserQuery userQuery) {
		checkIsAuthorized(userQuery);
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
	
	
	private static void checkIsAuthorized(UserQuery q){

		String securityUserName = "";
		
		SecurityContext sc = SecurityContextHolder.getContext();
		if (sc == null){
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}

		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a == null){
			throw new NotAuthorizedException("You must be logged in to access this resource");
		}
		
		if (a.getPrincipal() instanceof UserDetails) {
			UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
			securityUserName = currentUserDetails.getUsername();
		} else {
			securityUserName = a.getPrincipal().toString();
		}
		
		if (!q.getUsername().equals(securityUserName)) {
			throw new NotAuthorizedException(securityUserName + " is not authorized to modify this resource");
		}


	}
}
