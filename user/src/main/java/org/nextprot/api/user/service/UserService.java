package org.nextprot.api.user.service;

import java.util.List;

import org.nextprot.api.user.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService{
	
	List<User> getUserList();
	void updateUser(User user);

}
