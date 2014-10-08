package org.nextprot.api.user.dao;

import java.util.List;

import org.nextprot.api.user.domain.UserApplication;

public interface UserApplicationDao {

	List<UserApplication> getUserApplications(String username);

	UserApplication createUserApplication(UserApplication userApplication);

	UserApplication updateUserApplication(UserApplication userApplication);

	void deleteUserApplication(UserApplication userApplication);

	UserApplication getUserApplicationById(long id);

	UserApplication getUserApplication(long id);
	
}
