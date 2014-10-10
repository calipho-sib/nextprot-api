package org.nextprot.api.user.dao;

import java.util.List;

import org.nextprot.api.user.domain.User;

public interface UserDao {
	
	public List<User> getUserList();
	public void updateUser(User user);
	User getUserByUsername(String username);
	List<String> getUserAuthorities(String username);

}
