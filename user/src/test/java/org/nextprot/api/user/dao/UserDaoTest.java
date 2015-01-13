package org.nextprot.api.user.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserResourceBaseTest;
import org.nextprot.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "UserDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserDaoTest extends UserResourceBaseTest {

	@Autowired UserDao userDao;

	@Test
	public void testCreateAndGetUser() {

		User user = new User();

		user.setUsername("freud");

		long id = userDao.createUser(user);

		Assert.assertTrue(id > 0);

		user = userDao.getUserByUsername(user.getUsername());

		Assert.assertNull(user.getFirstName());
		Assert.assertNull(user.getLastName());
		Assert.assertTrue(user.getAuthorities().isEmpty());
	}

	@Test
	public void testCreateAndGetUserWithRole() {

		User user = new User();

		user.setUsername("freud");
		user.setFirstName("huber");
		user.setLastName("freud");

		Set<GrantedAuthority> hs = new HashSet<GrantedAuthority>();
		hs.add(new SimpleGrantedAuthority("ROLE_USER"));
		user.setAuthorities(hs);

		long id = userDao.createUser(user);

		Assert.assertTrue(id > 0);

		user = userDao.getUserByUsername(user.getUsername());

		Assert.assertEquals("huber", user.getFirstName());
		Assert.assertEquals("freud", user.getLastName());
		Assert.assertTrue(!user.getAuthorities().isEmpty());
		Assert.assertEquals(1, user.getAuthorities().size());
		Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	public void testReadUserWithRoles() {

		User user = userDao.getUserByUsername("spongebob");

		Assert.assertEquals(23, user.getId());
		Assert.assertEquals("spongebob", user.getUsername());
		Assert.assertNull(user.getFirstName());
		Assert.assertNull(user.getLastName());
		Assert.assertTrue(!user.getAuthorities().isEmpty());
		Assert.assertEquals(2, user.getAuthorities().size());
		Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
		Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void testReadUnknownUser() {

		userDao.getUserByUsername("superman");
	}

	@Test
	public void testReadUser2() {

		User user = userDao.getUserByUsername("tahitibob");

		Assert.assertEquals(24, user.getId());
		Assert.assertEquals("tahitibob", user.getUsername());
		Assert.assertEquals("tahiti", user.getFirstName());
		Assert.assertEquals("bob", user.getLastName());
		Assert.assertTrue(user.getAuthorities().isEmpty());
	}

	@Test
	public void testReadUsers() {

		List<User> users = userDao.getUserList();

		Assert.assertEquals(2, users.size());

		Assert.assertEquals(23, users.get(0).getId());
		Assert.assertEquals("spongebob", users.get(0).getUsername());
		Assert.assertNull(users.get(0).getFirstName());
		Assert.assertNull(users.get(0).getLastName());
		Assert.assertTrue(!users.get(0).getAuthorities().isEmpty());
		Assert.assertTrue(users.get(0).getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
		Assert.assertTrue(users.get(0).getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
		Assert.assertEquals(24, users.get(1).getId());
		Assert.assertEquals("tahitibob", users.get(1).getUsername());
		Assert.assertEquals("tahiti", users.get(1).getFirstName());
		Assert.assertEquals("bob", users.get(1).getLastName());
		Assert.assertTrue(users.get(1).getAuthorities().isEmpty());
	}

	@Test
	public void testUpdateUser() {

		User updated = new User();

		updated.setId(23L);
		updated.setUsername("spongebob");
		updated.setFirstName("sponge");
		updated.setLastName("bob");

		userDao.updateUser(updated);

		User user = userDao.getUserByUsername("spongebob");

		Assert.assertEquals(23, user.getId());
		Assert.assertEquals("spongebob", user.getUsername());
		Assert.assertEquals("sponge", user.getFirstName());
		Assert.assertEquals("bob", user.getLastName());

		Assert.assertTrue(!user.getAuthorities().isEmpty());
		Assert.assertEquals(2, user.getAuthorities().size());
		Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
		Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}

	@Test
	public void testUpdateUserWithRoles() {

		User updated = new User();

		updated.setId(23);
		updated.setUsername("spongebob");
		updated.setFirstName("sponge");
		updated.setLastName("bob");
		
		Set<GrantedAuthority> hs = new HashSet<GrantedAuthority>();
		hs.add(new SimpleGrantedAuthority("ROLE_USER"));

		updated.setAuthorities(hs);

		userDao.updateUser(updated);

		User user = userDao.getUserByUsername("spongebob");

		Assert.assertEquals(23, user.getId());
		Assert.assertEquals("spongebob", user.getUsername());
		Assert.assertEquals("sponge", user.getFirstName());
		Assert.assertEquals("bob", user.getLastName());
		Assert.assertTrue(!user.getAuthorities().isEmpty());
		Assert.assertEquals(1, user.getAuthorities().size());
		Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
	}

	@Test
	public void testDeleteUser() {

		User toDelete = new User();
		toDelete.setId(23);

		Assert.assertEquals(2, userDao.getUserList().size());

		userDao.deleteUser(toDelete);

		Assert.assertEquals(1, userDao.getUserList().size());
	}
}
