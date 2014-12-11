package org.nextprot.api.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.dao.UserDao;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.User;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.mockito.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/user-context-test.xml")
public class UserResourceAuthorizationServiceTest {

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private UserProteinListService userProteinListService;

    @Autowired
    private UserQueryDao userQueryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserApplicationDao userApplicationDao;

    @Autowired
    private UserProteinListDao userProteinListDao;

    @Mock
    private Authentication authentication;

    private User user;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        user = mockUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        dressMockedUserDao(userDao, user);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testCreateUserApplicationService() {

        UserApplication userApp = Mockito.mock(UserApplication.class);

        dressMockedUserApplication(userApp, "bobleponge", userApplicationDao);

        userApplicationService.createUserApplication(userApp);

        // TODO: check that aspect/checker correctly check authorization
        //Mockito.verify(checker).checkAuthorization(userApp);
    }

    @Test
    public void testUpdateUserQueryService() {

        UserQuery query = mockUserQuery("bobleponge");
        Mockito.when(userQueryDao.getUserQueryById(anyLong())).thenReturn(query);

        userQueryService.updateUserQuery(query);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdateUserQueryService2() {

        UserQuery query = mockUserQuery("bobbylapointe");
        Mockito.when(userQueryDao.getUserQueryById(anyLong())).thenReturn(query);

        userQueryService.updateUserQuery(query);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUpdateUserQueryService3() {

        UserQuery query = mockUserQuery("bobleponge");
        Mockito.when(userQueryDao.getUserQueryById(anyLong())).thenReturn(new UserQuery());

        userQueryService.updateUserQuery(query);
    }

    @Test
    public void testDeleteUserQueryService() {

        UserQuery query = mockUserQuery("bobleponge");
        Mockito.when(userQueryDao.getUserQueryById(anyLong())).thenReturn(query);

        userQueryService.deleteUserQuery(query);
    }

    @Test
    public void testCreateUserQueryService() {

        UserQuery query = mockUserQuery("bobleponge");
        Mockito.when(userQueryDao.getUserQueryById(anyLong())).thenReturn(query);

        userQueryService.createUserQuery(query);
    }

    @Test
    public void testCreateUserProteinListService() {

        UserProteinList proteinList =  Mockito.mock(UserProteinList.class);

        Mockito.when(proteinList.getOwnerName()).thenReturn("bobleponge");
        Mockito.when(proteinList.getOwnerId()).thenReturn(23L);
        Mockito.when(userProteinListDao.getUserProteinLists(anyString())).thenReturn(Arrays.asList(proteinList));

        userProteinListService.createUserProteinList(proteinList);
    }

    private static UserQuery mockUserQuery(String owner) {

        UserQuery query =  Mockito.mock(UserQuery.class);

        Mockito.when(query.getOwnerName()).thenReturn(owner);
        Mockito.when(query.getOwnerId()).thenReturn(23L);
        Mockito.when(query.getSparql()).thenReturn("orkfiejjgijrtwithi");

        return query;
    }

    private static void dressMockedUserApplication(UserApplication userApp, String owner, UserApplicationDao userApplicationDao) {

        Mockito.when(userApplicationDao.createUserApplication(userApp)).thenReturn(10L);
        Mockito.when(userApplicationDao.getUserApplicationById(anyLong())).thenReturn(userApp);
        Mockito.when(userApplicationDao.getUserApplicationListByOwnerId(anyInt())).thenReturn(Arrays.asList(new UserApplication()));

        Mockito.when(userApp.getOwnerName()).thenReturn(owner);
        Mockito.when(userApp.getOwnerId()).thenReturn(23L);
        Mockito.when(userApp.getId()).thenReturn(0L);
    }

    private static User mockUser() {

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("bobleponge");
        return user;
    }

    private static void dressMockedUserDao(UserDao userDao, User user) {

        Mockito.when(userDao.getUserByUsername(anyString())).thenReturn(user);
    }
}