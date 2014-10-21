package org.nextprot.api.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.security.NPSecurityContext;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Lazy
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

	@Autowired(required = false)
	private JWTCodec<Map<String, String>> codec;

	@Autowired
	private UserApplicationDao userApplicationDao;

	@Transactional
    public UserApplication createUserApplication(UserApplication userApplication) {

        NPreconditions.checkNotNull(userApplication, "The user application should not be null");
        NPreconditions.checkTrue(userApplication.getId() == 0L, "The user application id "+userApplication.getId()+" should not be defined");

        long id = userApplicationDao.createUserApplication(userApplication);

        UserApplication app = userApplicationDao.getUserApplicationById(id);

        app.setToken(generateToken(app));

        userApplicationDao.updateUserApplication(app);

        return app;
    }

    private String generateToken(UserApplication userApplication)  {

        Map<String, String> appProps = new HashMap<String, String>();
        appProps.put("id", String.valueOf(userApplication.getId()));
        appProps.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return codec.encodeJWT(appProps, 0);
    }

	/*public UserApplication createUserApplication(UserApplication userApplication) {
		
		//Check that the current user is allowed to perform the operation
		NPSecurityContext.checkUserAuthorization(userApplication);
		
		String token = tokenHelper.encodeJWT(userApplication, 365 * 60 * 60);
		userApplication.setToken(token);
		
		userApplicationDao.createUserApplication(userApplication);
		
		return userApplication;
	}*/

	@Override
	public List<UserApplication> getUserApplicationsByOwnerId(long ownerId) {
		List<UserApplication> apps = userApplicationDao.getUserApplicationListByOwnerId(ownerId);
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
