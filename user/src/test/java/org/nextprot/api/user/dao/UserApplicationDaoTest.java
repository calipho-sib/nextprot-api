package org.nextprot.api.user.dao;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import java.util.List;

@DatabaseSetup(value = "UserApplicationDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserApplicationDaoTest extends UserApplicationBaseTest {

	@Autowired UserApplicationDao userAppDao;

    @Test
    public void testCreateUserApplication() {

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

	@Test
	public void testReadUserApplication() {

		UserApplication app = userAppDao.getUserApplicationById(123456);

        Assert.assertEquals(123456, app.getId());
		Assert.assertEquals("mySuperApplication", app.getName());
        Assert.assertEquals("dsadas", app.getDescription());
        Assert.assertNull(app.getOrganisation());
        Assert.assertEquals("fredji", app.getResponsibleName());
        Assert.assertNull(app.getResponsibleEmail());
        Assert.assertNull(app.getWebsite());
        Assert.assertEquals(23L, app.getOwnerId());
        Assert.assertEquals("spongebob", app.getOwner());
        Assert.assertEquals("KLJBFAS", app.getToken());
        Assert.assertEquals("ACTIVE", app.getStatus());
        Assert.assertEquals("RO", app.getUserDataAccess());
        Assert.assertNull(app.getOrigins());
        Assert.assertEquals(app.getOwner(), app.getResourceOwner());
        Assert.assertNotNull(app.getCreationDate());
	}

    @Test
    public void testReadUserApplicationsByOwnerId() {

        List<UserApplication> apps = userAppDao.getUserApplicationListByOwnerId(23);

        Assert.assertTrue(!apps.isEmpty());
    }

    @Test
    public void testUpdateUserApplicationNoChange() {

        UserApplication app = userAppDao.getUserApplicationById(123456);

        userAppDao.updateUserApplication(app);

        app = userAppDao.getUserApplicationById(123456);

        Assert.assertEquals(123456, app.getId());
        Assert.assertEquals("mySuperApplication", app.getName());
        Assert.assertEquals("dsadas", app.getDescription());
        Assert.assertNull(app.getOrganisation());
        Assert.assertEquals("fredji", app.getResponsibleName());
        Assert.assertNull(app.getResponsibleEmail());
        Assert.assertNull(app.getWebsite());
        Assert.assertEquals(23L, app.getOwnerId());
        Assert.assertEquals("spongebob", app.getOwner());
        Assert.assertEquals("KLJBFAS", app.getToken());
        Assert.assertEquals("ACTIVE", app.getStatus());
        Assert.assertEquals("RO", app.getUserDataAccess());
        Assert.assertNull(app.getOrigins());
        Assert.assertEquals(app.getOwner(), app.getResourceOwner());
        Assert.assertNotNull(app.getCreationDate());
    }

    @Test
    public void testUpdateUserApplication() {

        UserApplication app = userAppDao.getUserApplicationById(123456);

        UserApplication updateApp = new UserApplication();

        updateApp.setId(app.getId());
        updateApp.setName("test app");
        updateApp.setDescription("a wonderful app");
        updateApp.setToken("TOKEN");

        userAppDao.updateUserApplication(updateApp);

        app = userAppDao.getUserApplicationById(123456);

        Assert.assertEquals(123456, app.getId());
        Assert.assertEquals("test app", app.getName());
        Assert.assertEquals("a wonderful app", app.getDescription());
        Assert.assertEquals("TOKEN", app.getToken());

        /*Assert.assertEquals(23L, app.getOwnerId());
        Assert.assertEquals("spongebob", app.getOwner());
        Assert.assertNull(app.getOrganisation());
        Assert.assertEquals("fredji", app.getResponsibleName());
        Assert.assertNull(app.getResponsibleEmail());
        Assert.assertNull(app.getWebsite());
        Assert.assertEquals(23L, app.getOwnerId());
        Assert.assertEquals("spongebob", app.getOwner());
        Assert.assertNull(app.getStatus());
        Assert.assertEquals("RO", app.getUserDataAccess());
        Assert.assertNull(app.getOrigins());
        Assert.assertEquals(app.getOwner(), app.getResourceOwner());
        Assert.assertNotNull(app.getCreationDate());*/
    }

    @Test(expected = NextProtException.class)
    public void testDeleteUserApplication() {

        UserApplication app = userAppDao.getUserApplicationById(123456);
        userAppDao.deleteUserApplication(app);

        userAppDao.getUserApplicationById(123456);
    }
}
