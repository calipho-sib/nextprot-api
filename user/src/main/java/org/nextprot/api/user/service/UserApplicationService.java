package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.UserApplication;

public interface UserApplicationService {

	public List<UserApplication> getUserApplications(String username);
	public UserApplication createUserApplication(UserApplication userApplication);
	public UserApplication getUserApplication(long id);
	public void deleteApplication(Long id);
	
}
