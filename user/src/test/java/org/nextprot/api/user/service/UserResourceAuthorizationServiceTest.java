package org.nextprot.api.user.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.nextprot.api.user.domain.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/user-context-test.xml")
public class UserResourceAuthorizationServiceTest {

    @Autowired
    UserQueryService uqService;

    @Autowired
    UserApplicationService uaService;

    @Autowired
    UserProteinListService uplService;

    @Mock
    UserQueryDao uqDao;

    @Mock
    UserApplicationDao uaDao;

    @Mock
    UserProteinListDao uplDao;

    @Mock
    JWTCodec<Map<String, String>> codec;

    @Mock
    Authentication authentication;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        mockUserDetails(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test(expected = NextProtException.class)
    public void testCreateUserApplicationServiceSetId() {

        UserApplication userApp = new UserApplication();
        userApp.setId(2);

        uaService.createUserApplication(userApp);
    }

    @Test
    public void testCreateUserApplicationService() {

        UserApplication userApp = Mockito.mock(UserApplication.class);

        Mockito.when(userApp.getResourceOwner()).thenReturn("bobleponge");
        Mockito.when(userApp.getOwnerId()).thenReturn(666L);

        Mockito.when(uaDao.createUserApplication(userApp)).thenReturn(10L);
        Mockito.when(uaDao.getUserApplicationById(10L)).thenReturn(userApp);
        Mockito.when(codec.encodeJWT(any(Map.class), anyInt())).thenReturn("pifpafpouf");

        UserApplication app = uaService.createUserApplication(userApp);

        Mockito.verify(uaDao).updateUserApplication(userApp);

        Assert.assertEquals("pifpafpouf", app.getToken());
    }

    @Test
    public void testUpdateUserQueryService() {

        UserQuery query =  Mockito.mock(UserQuery.class);

        Mockito.when(query.getResourceOwner()).thenReturn("bobleponge");
        Mockito.when(query.getOwnerId()).thenReturn(666L);
        Mockito.when(query.getSparql()).thenReturn("orkfiejjgijrtwithi");
        Mockito.when(uqDao.getUserQueryById(anyLong())).thenReturn(query);

        uqService.updateUserQuery(query);
    }

    @Test
    public void testCreateUserQueryService() {

        UserQuery query =  Mockito.mock(UserQuery.class);

        Mockito.when(query.getResourceOwner()).thenReturn("bobleponge");
        Mockito.when(query.getOwnerId()).thenReturn(666L);
        Mockito.when(query.getSparql()).thenReturn("orkfiejjgijrtwithi");
        Mockito.when(uqDao.getUserQueryById(anyLong())).thenReturn(query);

        uqService.createUserQuery(query);
    }

    @Test
    public void testCreateUserProteinListService() {

        UserProteinList proteinList =  Mockito.mock(UserProteinList.class);

        Mockito.when(proteinList.getResourceOwner()).thenReturn("bobleponge");
        Mockito.when(proteinList.getOwnerId()).thenReturn(666L);
        Mockito.when(uplDao.getUserProteinLists(anyString())).thenReturn(Arrays.asList(proteinList));

        uplService.createUserProteinList(proteinList);
    }

    private static UserDetails mockUserDetails(Authentication authentication) {

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("bobleponge");
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        return userDetails;
    }

    private static <T extends UserResource> T mockUserResource(Class<T> clazz) {

        T userResource =  Mockito.mock(clazz);

        Mockito.when(userResource.getResourceOwner()).thenReturn("bobleponge");
        //Mockito.when(userResource.getOwnerId()).thenReturn(666L);

        return userResource;
    }
}