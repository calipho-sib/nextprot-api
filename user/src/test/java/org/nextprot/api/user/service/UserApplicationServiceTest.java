package org.nextprot.api.user.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.domain.UserApplication;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

public class UserApplicationServiceTest extends AbstractUnitBaseTest {

    // Check why the following annotation could be problematic !!!!!
    // http://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/
    @InjectMocks
    @Autowired
    UserApplicationService service;

    @Mock
    UserApplicationDao dao;

    @Mock
    JWTCodec<Map<String, String>> codec;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NextProtException.class)
    public void testCreateUserApplicationServiceSetId() {

        UserApplication userApp = new UserApplication();
        userApp.setId(2);

        service.createUserApplication(userApp);
    }

    @Test
    public void testCreateUserApplicationService() {

        UserApplication userApp = new UserApplication();

        userApp.setName("test app");
        userApp.setDescription("a wonderful app");
        userApp.setOwnerId(23);
        userApp.setToken("");

        Mockito.when(dao.createUserApplication(userApp)).thenReturn(10L);
        Mockito.when(dao.getUserApplicationById(10L)).thenReturn(userApp);
        Mockito.when(codec.encodeJWT(any(Map.class), anyInt())).thenReturn("pifpafpouf");

        UserApplication app = service.createUserApplication(userApp);

        Mockito.verify(dao).updateUserApplication(userApp);

        Assert.assertEquals("pifpafpouf", app.getToken());
    }
}