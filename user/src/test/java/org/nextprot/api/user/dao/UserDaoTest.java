package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

//@TransactionConfiguration(defaultRollback = false)
@DatabaseSetup(value = "UserDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserDaoTest extends UserApplicationBaseTest {

    @Autowired
    UserDao userDao;

    @Test
    public void testCreateAndGetUser() {

        User user = new User();

        user.setUsername("freud");

        long id = userDao.createUser(user);

        Assert.assertTrue(id > 0);

        user = userDao.getUserByUsername(user.getUsername());

        Assert.assertNull(user.getFirstName());
        Assert.assertNull(user.getLastName());
        Assert.assertTrue(user.getRoles().isEmpty());
    }

    @Test
    public void testCreateAndGetUserWithRole() {

        User user = new User();

        user.setUsername("freud");
        user.setFirstName("huber");
        user.setLastName("freud");
        user.setRoles(Sets.newHashSet("USER"));

        long id = userDao.createUser(user);

        Assert.assertTrue(id>0);

        user = userDao.getUserByUsername(user.getUsername());

        Assert.assertEquals("huber", user.getFirstName());
        Assert.assertEquals("freud", user.getLastName());
        Assert.assertTrue(!user.getRoles().isEmpty());
        Assert.assertEquals(1, user.getRoles().size());
        Assert.assertTrue(user.getRoles().contains("USER"));
    }

    @Test
    public void testReadUserWithRoles() {

        User user = userDao.getUserByUsername("spongebob");

        Assert.assertEquals(23, user.getId());
        Assert.assertEquals("spongebob", user.getUsername());
        Assert.assertNull(user.getFirstName());
        Assert.assertNull(user.getLastName());
        Assert.assertTrue(!user.getRoles().isEmpty());
        Assert.assertTrue(user.getRoles().contains("USER"));
    }

    @Test
    public void testReadUser2() {

        User user = userDao.getUserByUsername("tahitibob");

        Assert.assertEquals(24, user.getId());
        Assert.assertEquals("tahitibob", user.getUsername());
        Assert.assertEquals("tahiti", user.getFirstName());
        Assert.assertEquals("bob", user.getLastName());
    }

    @Test
    public void testReadUsers() {

        Assert.fail("to test");
        /*List<User> users = userDao.getUserList();

        Assert.assertEquals(2, users.size());*/
    }

    @Test
    public void testUpdateUser() {

        Assert.fail("to test");
        /*User user = userDao.getUserByUsername("spongebob");

        userDao.updateUser(user);*/
    }

    @Test
    public void testDeleteUser() {

        Assert.fail("to test");
        /*User user = userDao.getUserByUsername("spongebob");

        userDao.deleteUser(user);*/
    }
}