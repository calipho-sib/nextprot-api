package org.nextprot.api.user.service;

import org.nextprot.api.user.domain.UserApplication;

import java.util.List;

public interface UserApplicationService {

    List<UserApplication> getUserApplicationsByOwnerId(long id);

    UserApplication createUserApplication(UserApplication userApplication);

    UserApplication getUserApplication(long id);

    void deleteApplication(long id);
}
