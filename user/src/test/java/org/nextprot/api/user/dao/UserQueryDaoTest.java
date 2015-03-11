package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserResourceBaseTest;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

import static org.junit.Assert.*;

@DatabaseSetup(value = "UserQueriesDaoTest.xml", type = DatabaseOperation.CLEAN_INSERT)
public class UserQueryDaoTest extends UserResourceBaseTest {

    @Autowired private UserQueryDao userQueryDao;

    @Test
    public void testGetUserQueries() {

        List<UserQuery> list = userQueryDao.getUserQueries("spongebob");

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(2, list.size());

        assertExpectedUserQuery(list.get(0), 15, "spongebob", "myquery", "my first query", false, "sparql query", "00000001", new HashSet<String>());
        assertExpectedUserQuery(list.get(1), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", "ZZZZZU8V", Sets.newHashSet("public"));
    }

    @Test
    public void testGetUserQueryById() {

        UserQuery userQuery = userQueryDao.getUserQueryById(15);

        assertExpectedUserQuery(userQuery, 15, "spongebob", "myquery", "my first query", false, "sparql query", "00000001", new HashSet<String>());
    }

    @Test
    public void testGetUserQueryByPublicId() {

        UserQuery userQuery = userQueryDao.getUserQueryByPublicId("00000001");

        assertExpectedUserQuery(userQuery, 15, "spongebob", "myquery", "my first query", false, "sparql query", "00000001", new HashSet<String>());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testGetUserQueryByPublicIdNotFound() {

        userQueryDao.getUserQueryByPublicId("00000005");
    }

    @Test
    public void testGetUserQueriesByTag() {

        List<UserQuery> list = userQueryDao.getUserQueriesByTag("public");

        assertNotNull(list);
        assertTrue(!list.isEmpty());
        assertEquals(1, list.size());

        assertExpectedUserQuery(list.get(0), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", "ZZZZZU8V", Sets.newHashSet("public"));
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

        assertExpectedUserQuery(list.get(0), 16, "spongebob", "myquery2", "my second query", true, "another sparql query", "ZZZZZU8V", Sets.newHashSet("public"));
    }

    @Test
    public void testGetTagsByQueryId() {

        Map<Long, Set<String>> tags = userQueryDao.getQueryTags(Arrays.asList(16L));

        assertEquals(1, tags.size());
        assertEquals(Sets.newHashSet("public"), tags.get(16L));
    }

    @Test
    public void testGetTagsByUnknownQueryId() {

        Map<Long, Set<String>> tags = userQueryDao.getQueryTags(Arrays.asList(17L));

        assertTrue(!tags.isEmpty());
        assertEquals(Sets.<String>newHashSet(), tags.get(17L));
    }

    @Test
    public void testGetTagsByUnknownQueryIds() {

        Map<Long, Set<String>> tags = userQueryDao.getQueryTags(Arrays.asList(16L, 17L));

        assertTrue(!tags.isEmpty());
        assertEquals(Sets.newHashSet("public"), tags.get(16L));
        assertEquals(Sets.<String>newHashSet(), tags.get(17L));
    }

    @Test
    public void testCreateUserQuery() {

        UserQuery query = new UserQuery();

        query.setTitle("ma requete");
        query.setSparql("yet another sparql query");
        query.setPublicId("00000002");
        query.setOwnerId(24);

        long id = userQueryDao.createUserQuery(query);
        assertTrue(id > 0);

        UserQuery query2 = userQueryDao.getUserQueryById(id);

        assertExpectedUserQuery(query2, id, "tahitibob", "ma requete", null, false, "yet another sparql query", "00000002", Sets.<String>newHashSet());
    }

    @Test
    public void testCreateUserQuery2() {

        UserQuery query = new UserQuery();

        query.setTitle("ma requete");
        query.setSparql("yet another sparql query");
        query.setPublicId("00000002");
        query.setOwnerId(24);

        long id = userQueryDao.createUserQuery(query);
        assertTrue(id > 0);

        UserQuery query2 = userQueryDao.getUserQueryById(id);

        assertExpectedUserQuery(query2, id, "tahitibob", "ma requete", null, false, "yet another sparql query", "00000002", Sets.<String>newHashSet());
    }

    @Test
    public void testCreateUserQueryAllField() {

        UserQuery query = new UserQuery();

        query.setTitle("ma requete");
        query.setDescription("une simple requete");
        query.setSparql("yet another sparql query");
        query.setPublished(true);
        query.setPublicId("00000003");
        query.setOwnerId(24);

        long id = userQueryDao.createUserQuery(query);
        assertTrue(id > 0);

        UserQuery query2 = userQueryDao.getUserQueryById(id);

        assertExpectedUserQuery(query2, id, "tahitibob", "ma requete", "une simple requete", true, "yet another sparql query", "00000003", Sets.<String>newHashSet());
    }

    @Test
    public void testCreateUserQueryTags() {

        userQueryDao.createUserQueryTags(16, Sets.newHashSet("great", "heavy"));

        UserQuery query = userQueryDao.getUserQueryById(16);

        assertExpectedUserQuery(query, 16, "spongebob", "myquery2", "my second query", true, "another sparql query", "ZZZZZU8V", Sets.newHashSet("public", "great", "heavy"));
    }

    @Test
    public void testDeleteUserQueryTags() {

        Set<String> accs = new HashSet<String>();
        accs.add("public");

        int count = userQueryDao.deleteUserQueryTags(16, accs);

        UserQuery query = userQueryDao.getUserQueryById(16);

        assertEquals(1, count);

        assertExpectedUserQuery(query, 16, "spongebob", "myquery2", "my second query", true, "another sparql query", "ZZZZZU8V", Sets.<String>newHashSet());
    }

    @Test
    public void testDeleteUserQueryTags2() {

        Set<String> accs = new HashSet<String>();
        accs.add("pim");
        accs.add("pam");
        accs.add("poum");

        int count = userQueryDao.deleteUserQueryTags(16, accs);

        UserQuery query = userQueryDao.getUserQueryById(16);

        assertEquals(0, count);

        assertExpectedUserQuery(query, 16, "spongebob", "myquery2", "my second query", true, "another sparql query", "ZZZZZU8V", Sets.newHashSet("public"));
    }

    @Test
    public void testDeleteUserQuery() {

        int count = userQueryDao.deleteUserQuery(16);

        assertEquals(1, count);
        assertEquals(1, userQueryDao.getUserQueries("spongebob").size());
        Map<Long, Set<String>> tags = userQueryDao.getQueryTags(Arrays.asList(16L));

        assertTrue(tags.get(16L).isEmpty());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testDeleteUserQuery2() {

        int count = userQueryDao.deleteUserQuery(17);

        assertEquals(0, count);
        userQueryDao.getUserQueryById(17);
    }

    @Test
    public void testUpdateUserQuery() {

        UserQuery query = new UserQuery();

        query.setUserQueryId(16);
        query.setTitle("ma requete");
        query.setDescription("une simple requete");
        query.setSparql("yet another sparql query");
        query.setPublished(true);

        userQueryDao.updateUserQuery(query);

        query  = userQueryDao.getUserQueryById(16);

        assertExpectedUserQuery(query, 16, "spongebob", "ma requete", "une simple requete", true, "yet another sparql query", "ZZZZZU8V", Sets.newHashSet("public"));
    }

    private static void assertExpectedUserQuery(UserQuery userQuery, long expectedUserQueryId, String expectedOwner, String expectedTitle,
                                                String expectedDescription, boolean expectedPublished, String expectedSparql, String expectedPublicId,
                                                Set<String> expectedTags) {
        assertNotNull(userQuery);
        assertEquals(expectedUserQueryId, userQuery.getUserQueryId());
        assertEquals(expectedOwner, userQuery.getOwner());
        assertEquals(expectedOwner, userQuery.getOwnerName());
        assertEquals(expectedDescription, userQuery.getDescription());
        assertEquals(expectedTitle, userQuery.getTitle());
        assertTrue(userQuery.getPublished() == expectedPublished);
        assertEquals(expectedSparql, userQuery.getSparql());
        assertEquals(expectedPublicId, userQuery.getPublicId());
        assertEquals(expectedTags, userQuery.getTags());
    }
}