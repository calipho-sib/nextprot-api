package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.security.service.impl.NPSecurityContext;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.service.UserApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service
public class UserApplicationServiceImpl implements UserApplicationService {

	@Autowired
	private JWTCodec<Map<String, Object>> codec;

	@Autowired
	private UserApplicationDao userApplicationDao;

	@Transactional
    public UserApplication createUserApplication(UserApplication userApplication) {

        NPreconditions.checkNotNull(userApplication, "The user application should not be null");
		NPreconditions.checkTrue(!userApplication.isPersisted(), "The user application should be new");

        long id = userApplicationDao.createUserApplication(userApplication);

        UserApplication app = userApplicationDao.getUserApplicationById(id);

        app.setToken(generateToken(app));

        userApplicationDao.updateUserApplication(app);

        return app;
    }

    private String generateToken(UserApplication userApplication)  {

        Map<String, Object> appProps = new HashMap<String, Object>();
        appProps.put("id", String.valueOf(userApplication.getId()));
        appProps.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return codec.encodeJWT(appProps, 0);
    }

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
	public void deleteApplication(long id) {
		UserApplication app = this.userApplicationDao.getUserApplicationById(id);
		//TODO make checking that is the current user or admin...
		this.userApplicationDao.deleteUserApplication(app);
	}
}
