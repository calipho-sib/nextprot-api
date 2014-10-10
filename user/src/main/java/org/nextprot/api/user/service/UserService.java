package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.User;


public interface UserService {
	
	List<User> getUserList();
	void updateUser(User user);

}
