package org.nextprot.api.user.service.impl;

import java.util.List;

import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.security.NPSecurityContext;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sib.calipho.spring.security.auth0.Auth0TokenHelper;

@Lazy
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

	@Autowired(required = false)
	private Auth0TokenHelper<Object> tokenHelper;

	@Autowired
	private UserApplicationDao userApplicationDao;

	@Transactional
	public UserApplication createUserApplication(UserApplication userApplication) {
		
		//Check that the current user is allowed to perform the operation
		NPSecurityContext.checkUserAuthorization(userApplication);
		
		String token = tokenHelper.generateToken(userApplication, 365 * 60 * 60);
		userApplication.setToken(token);
		
		userApplicationDao.createUserApplication(userApplication);
		
		return userApplication;

	}

	@Override
	public List<UserApplication> getUserApplicationsByOwnerId(long ownerId) {
		List<UserApplication> apps = userApplicationDao.getUserApplicationsByOwnerId(ownerId);
		NPSecurityContext.checkUserAuthorization(apps); //will throw an exception if not authorized
		return apps;
	}

	@Override
	public UserApplication getUserApplication(long id) {
		return this.userApplicationDao.getUserApplicationById(id);
	}

	@Override
	public void deleteApplication(Long id) {
		UserApplication app = this.userApplicationDao.getUserApplicationById(id);
		//TODO make checking that is the current user or admin...
		this.userApplicationDao.deleteUserApplication(app);
	}

}
