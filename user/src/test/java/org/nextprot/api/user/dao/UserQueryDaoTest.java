package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@DatabaseSetup(value = "UserQueriesDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserQueryDaoTest extends UserApplicationBaseTest {

    @Autowired private UserQueryDao userQueryDao;

    @Test
    public void testGetUserQueries() {

        List<UserQuery> lists = userQueryDao.getUserQueries("spongebob");

        assertNotNull(lists);
        assertTrue(!lists.isEmpty());
        assertEquals(2, lists.size());

        assertEquals("spongebob", lists.get(0).getUsername());
        assertEquals("spongebob", lists.get(0).getResourceOwner());
        assertEquals("myquery", lists.get(0).getTitle());
        assertEquals("my first query", lists.get(0).getDescription());
        assertEquals(15, lists.get(0).getUserQueryId());
        assertEquals("sparql query", lists.get(0).getSparql());
        assertTrue(!lists.get(0).getPublished());
        assertTrue(lists.get(0).getTags().isEmpty());

        assertEquals("spongebob", lists.get(1).getUsername());
        assertEquals("spongebob", lists.get(1).getResourceOwner());
        assertEquals("myquery2", lists.get(1).getTitle());
        assertEquals("my second query", lists.get(1).getDescription());
        assertEquals(16, lists.get(1).getUserQueryId());
        assertEquals("another sparql query", lists.get(1).getSparql());
        assertTrue(lists.get(1).getPublished());
        assertEquals(Sets.newHashSet("public"), lists.get(1).getTags());
    }


}