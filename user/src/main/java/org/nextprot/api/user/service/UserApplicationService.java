package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.UserApplication;

public interface UserApplicationService {

    List<UserApplication> getUserApplicationsByOwnerId(long id);

    UserApplication createUserApplication(UserApplication userApplication);

    UserApplication getUserApplication(long id);

    void deleteApplication(Long id);
	
}
