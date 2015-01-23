package org.nextprot.api.user.dao;

import org.nextprot.api.user.domain.UserApplication;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface UserApplicationDao {

    List<UserApplication> getUserApplicationListByOwnerId(long userId);

	long createUserApplication(UserApplication userApplication);

    void updateUserApplication(UserApplication userApplication);

	void deleteUserApplication(UserApplication userApplication);

	UserApplication getUserApplicationById(long id) throws DataAccessException;
	
}
