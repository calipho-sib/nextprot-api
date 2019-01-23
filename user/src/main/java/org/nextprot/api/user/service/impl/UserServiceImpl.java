package org.nextprot.api.user.service.impl;

import org.nextprot.api.commons.resource.AllowedAnonymous;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.domain.User;
import org.nextprot.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

	@Autowired UserDao userDao;

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<User> getUserList() {
		return userDao.getUserList();
	}

	@Override
	public User getUser(String username) {
		return userDao.getUserByUsername(username);
	}

	
	@Override
	@PreAuthorize("hasRole('ROLE_USER')") //TODO  && isCurrentUser()
	@CacheEvict(value = "read-user", key = "#username")
	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	@Override
	@Cacheable(value = "read-user", key = "#username", sync = true)
	@AllowedAnonymous
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return userDao.getUserByUsername(username);
		} catch(EmptyResultDataAccessException e) {

			User user = new User();
			user.setUsername(username);
			Set<GrantedAuthority> hs = new HashSet<GrantedAuthority>();
			hs.add(new SimpleGrantedAuthority("ROLE_USER"));
			user.setAuthorities(hs);
			createUser(user);
		}

		return userDao.getUserByUsername(username);
	}

	@Override
	@CacheEvict(value = "read-user", key = "#username")
	public void createUser(User user) {
		userDao.createUser(user);
	}

}


