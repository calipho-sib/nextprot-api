package org.nextprot.api.user.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "UserApplicationDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserApplicationDaoTest extends UserApplicationBaseTest {

	@Autowired UserApplicationDao userAppDao;

	@Test
	public void shouldGetUserApplicationAsExpected() {

		UserApplication app = userAppDao.getUserApplicationById(123456);

		Assert.assertEquals("mySuperApplication", app.getName());
        Assert.assertEquals(23L, app.getOwnerId());
        Assert.assertEquals("spongebob", app.getOwner());
	}

    @Test
	public void shouldCreateAndGetUserApplication() {

		UserApplication app = new UserApplication();

		app.setName("test app");
        app.setDescription("a wonderful app");
        app.setOwnerId(23);
        app.setToken("");

        long id = userAppDao.createUserApplication(app);

		UserApplication app2 = userAppDao.getUserApplicationById(id);

        Assert.assertEquals("test app", app2.getName());
        Assert.assertEquals("a wonderful app", app2.getDescription());
        Assert.assertEquals(23L, app2.getOwnerId());
        Assert.assertEquals("spongebob", app2.getOwner());
	}

}
