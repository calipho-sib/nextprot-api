package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@DatabaseSetup(value = "UserQueriesDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserQueryDaoTest extends UserApplicationBaseTest {

    @Autowired private UserQueryDao userQueryDao;

    @Test
    public void testGetUserQueries() {

        List<UserQuery> list = userQueryDao.getUserQueries("spongebob");

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(2, list.size());

        assertExpectedUserQuery(list.get(0), 15, "spongebob", "myquery", "my first query", false, "sparql query", new HashSet<String>());
        assertExpectedUserQuery(list.get(1), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testGetUserQueryById() {

        UserQuery userQuery = userQueryDao.getUserQueryById(15);

        assertExpectedUserQuery(userQuery, 15, "spongebob", "myquery", "my first query", false, "sparql query", new HashSet<String>());
    }

    @Test
    public void testGetUserQueriesByTag() {

        List<UserQuery> list = userQueryDao.getUserQueriesByTag("public");

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(1, list.size());

        assertExpectedUserQuery(list.get(0), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testGetUserQueriesByUnknownTag() {

        List<UserQuery> list = userQueryDao.getUserQueriesByTag("publication");

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGetPublishedQueries() {

        List<UserQuery> list = userQueryDao.getPublishedQueries();

        assertNotNull(list);
        assertTrue(!list.isEmpty());

        assertEquals(1, list.size());

        assertExpectedUserQuery(list.get(0), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", Sets.newHashSet("public"));
    }

    @Test
    public void testCreateUserQuery() {

        Assert.fail("implement this test");

        UserQuery query = new UserQuery();

        userQueryDao.createUserQuery(query);
    }

    private static void assertExpectedUserQuery(UserQuery userQuery, long expectedUserQueryId, String expectedOwner, String expectedTitle,
                                                String expectedDescription, boolean expectedPublished, String expectedSparql,
                                                Set<String> expectedTags) {

        assertEquals(expectedUserQueryId, userQuery.getUserQueryId());
        assertEquals(expectedOwner, userQuery.getUsername());
        assertEquals(expectedOwner, userQuery.getResourceOwner());
        assertEquals(expectedDescription, userQuery.getDescription());
        assertEquals(expectedTitle, userQuery.getTitle());
        assertTrue(userQuery.getPublished() == expectedPublished);
        assertEquals(expectedSparql, userQuery.getSparql());
        assertEquals(expectedTags, userQuery.getTags());
    }
}