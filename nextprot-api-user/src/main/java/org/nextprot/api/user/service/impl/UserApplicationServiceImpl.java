package org.nextprot.api.user.service.impl;

import java.util.List;

import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.service.UserApplicationService;
import org.nextprot.api.web.security.NPSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sib.calipho.spring.security.auth0.Auth0TokenHelper;

@Lazy
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

	@Autowired
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
	public List<UserApplication> getUserApplications(String username) {
		List<UserApplication> apps = this.userApplicationDao.getUserApplications(username);
		NPSecurityContext.checkUserAuthorization(apps); //will throw an exception if not authorized
		return apps;
	}

	@Override
	public UserApplication getUserApplication(long id) {
		return this.userApplicationDao.getUserApplication(id);
	}

	@Override
	public void deleteApplication(Long id) {
		this.userApplicationDao.deleteApplication(id);
	}

}
