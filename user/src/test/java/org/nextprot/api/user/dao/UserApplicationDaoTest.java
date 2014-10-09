package org.nextprot.api.user.dao;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

//@TransactionConfiguration(defaultRollback = false)
@DatabaseSetup(value = "UserApplicationDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserApplicationDaoTest extends UserApplicationBaseTest {

	@Autowired UserApplicationDao userAppDao;

	@Test
	public void shouldGetUserApplicationAsExpected() {
		// Will get what is defined on the UserApplicationDaoTest
		UserApplication app = userAppDao.getUserApplicationById(123456);

		Assert.assertEquals("mySuperApplication", app.getName());
        Assert.assertEquals(23L, app.getOwnerId());
        Assert.assertEquals("spongebob", app.getOwner());
	}

	@Ignore
	public void shouldCreateAndGetUserApplication() {
		UserApplication app = new UserApplication();
		app.setName("some name");
		// app.setName("some name");
		// app.setName("some name");
		// app.setName("some name");
		// app.setName("some name");

		UserApplication app1 = userAppDao.createUserApplication(app);
		UserApplication app2 = userAppDao.getUserApplicationById(app1.getId());

		Assert.assertEquals(app1, app2);

	}

}
