package org.nextprot.api.user.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.user.aop.UserApplicationAuthorizationChecker;
import org.nextprot.api.user.aop.UserResourceAuthorizationAspect;
import org.nextprot.api.user.dao.UserApplicationDao;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserApplication;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private UserApplicationDao userApplicationDao;

    @Autowired
    private UserProteinListDao userProteinListDao;

    @Mock
    private Authentication authentication;

    @Autowired
    private UserResourceAuthorizationAspect aspect;

    @Autowired
    private UserApplicationAuthorizationChecker checker;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        dressMockedAuthentication(authentication);
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

    @Test(expected = NotAuthorizedException.class)
    public void testUpdateUserQueryService4() {

        UserQuery query = mockUserQuery("bobleponge");
        UserQuery query2 = mockUserQuery("joelindien");

        Mockito.when(userQueryDao.getUserQueryById(anyLong())).thenReturn(query2);

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

        Mockito.when(proteinList.getResourceOwner()).thenReturn("bobleponge");
        Mockito.when(proteinList.getOwnerId()).thenReturn(23L);
        Mockito.when(userProteinListDao.getUserProteinLists(anyString())).thenReturn(Arrays.asList(proteinList));

        userProteinListService.createUserProteinList(proteinList);
    }

    private static UserQuery mockUserQuery(String owner) {

        UserQuery query =  Mockito.mock(UserQuery.class);

        Mockito.when(query.getResourceOwner()).thenReturn(owner);
        Mockito.when(query.getOwnerId()).thenReturn(23L);
        Mockito.when(query.getSparql()).thenReturn("orkfiejjgijrtwithi");

        return query;
    }

    private static void dressMockedUserApplication(UserApplication userApp, String owner, UserApplicationDao userApplicationDao) {

        Mockito.when(userApplicationDao.createUserApplication(userApp)).thenReturn(10L);
        Mockito.when(userApplicationDao.getUserApplicationById(anyLong())).thenReturn(userApp);
        Mockito.when(userApplicationDao.getUserApplicationListByOwnerId(anyInt())).thenReturn(Arrays.asList(new UserApplication()));

        Mockito.when(userApp.getResourceOwner()).thenReturn(owner);
        Mockito.when(userApp.getOwnerId()).thenReturn(23L);
        Mockito.when(userApp.getId()).thenReturn(0L);
    }

    private static void dressMockedAuthentication(Authentication authentication) {

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("bobleponge");

        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
    }
}