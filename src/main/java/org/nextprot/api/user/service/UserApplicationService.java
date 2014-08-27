package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.UserApplication;

public interface UserApplicationService {

	public UserApplication createUserApplication();
	public List<UserApplication> getUserApplications();
	
}
