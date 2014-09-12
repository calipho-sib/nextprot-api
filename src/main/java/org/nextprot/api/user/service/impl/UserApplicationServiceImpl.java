package org.nextprot.api.user.service.impl;

import java.util.List;

import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.security.NPSecurityContext;
import org.nextprot.api.user.security.UserApplicationKeyGenerator;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Lazy
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

	@Autowired
	private UserApplicationKeyGenerator keyGenerator;

	@Autowired
	private UserApplicationDao userApplicationDao;

	@Transactional
	public UserApplication createUserApplication(UserApplication userApplication) {
		
		//Check that the current user is allowed to perform the operation
		NPSecurityContext.checkUserAuthorization(userApplication);
		
		String token = keyGenerator.generateToken(userApplication);
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

}
