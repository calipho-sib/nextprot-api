package org.nextprot.api.user.dao.impl;

import com.google.common.collect.ImmutableMap;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.user.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class UsersExtractorTest {

    @Test
    public void testExtractData() throws Exception {

        UsersExtractor extractor = new UsersExtractor();

        List<Map<String, ?>> records = new ArrayList<Map<String, ?>>();

        records.add(ImmutableMap.of("user_id", 24L, "user_name", "tahitibob", "first_name", "tahiti", "last_name", "bob", "role_name", ""));
        records.add(ImmutableMap.of("user_id", 23L, "user_name", "spongebob", "first_name", "", "last_name", "", "role_name", "ROLE_USER"));
        records.add(ImmutableMap.of("user_id", 23L, "user_name", "spongebob", "first_name", "", "last_name", "", "role_name", "ROLE_ADMIN"));

        List<User> users = extractor.extractData(mockResultSet(records));

        Assert.assertEquals(2, users.size());
        User user = users.get(0);
        Assert.assertEquals(24, user.getId());
        Assert.assertEquals("tahitibob", user.getUsername());
        Assert.assertEquals("tahiti", user.getFirstName());
        Assert.assertEquals("bob", user.getLastName());
        Assert.assertTrue(user.getAuthorities().isEmpty());
        user = users.get(1);
        Assert.assertEquals(23, user.getId());
        Assert.assertEquals("spongebob", user.getUsername());
        Assert.assertNull(user.getFirstName());
        Assert.assertNull(user.getLastName());
        Assert.assertTrue(!user.getAuthorities().isEmpty());
        Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void testExtractDataSortedByUsername() throws Exception {

        UsersExtractor extractor = new UsersExtractor(true);

        List<Map<String, ?>> records = new ArrayList<Map<String, ?>>();

        records.add(ImmutableMap.of("user_id", 24L, "user_name", "tahitibob", "first_name", "tahiti", "last_name", "bob", "role_name", ""));
        records.add(ImmutableMap.of("user_id", 23L, "user_name", "spongebob", "first_name", "", "last_name", "", "role_name", "ROLE_USER"));
        records.add(ImmutableMap.of("user_id", 23L, "user_name", "spongebob", "first_name", "", "last_name", "", "role_name", "ROLE_ADMIN"));

        List<User> users = extractor.extractData(mockResultSet(records));

        Assert.assertEquals(2, users.size());
        User user = users.get(1);
        Assert.assertEquals(24, user.getId());
        Assert.assertEquals("tahitibob", user.getUsername());
        Assert.assertEquals("tahiti", user.getFirstName());
        Assert.assertEquals("bob", user.getLastName());
        Assert.assertTrue(user.getAuthorities().isEmpty());
        user = users.get(0);
        Assert.assertEquals(23, user.getId());
        Assert.assertEquals("spongebob", user.getUsername());
        Assert.assertNull(user.getFirstName());
        Assert.assertNull(user.getLastName());
        Assert.assertTrue(!user.getAuthorities().isEmpty());
        Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        Assert.assertTrue(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private ResultSet mockResultSet(List<Map<String, ?>> records) throws SQLException {

        ResultSet rs = mock(ResultSet.class);

        Boolean[] hasNexts = new Boolean[records.size()+1];
        Long[] ids = new Long[records.size()];
        String[] userNames = new String[records.size()];
        String[] firstNames = new String[records.size()];
        String[] lastNames = new String[records.size()];
        String[] roleNames = new String[records.size()];

        for (int i=0 ; i<records.size() ; i++) {

            hasNexts[i] = true;
            ids[i] = (Long) records.get(i).get("user_id");
            userNames[i] = (String) records.get(i).get("user_name");
            firstNames[i] = (String) records.get(i).get("first_name");
            lastNames[i] = (String) records.get(i).get("last_name");
            roleNames[i] = (String) records.get(i).get("role_name");

            if (firstNames[i].isEmpty()) firstNames[i] = null;
            if (lastNames[i].isEmpty()) lastNames[i] = null;
            if (roleNames[i].isEmpty()) roleNames[i] = null;
        }
        hasNexts[records.size()] = false;

        given(rs.next()).willReturn(hasNexts[0], Arrays.copyOfRange(hasNexts, 1, hasNexts.length));
        given(rs.getLong("user_id")).willReturn(ids[0], Arrays.copyOfRange(ids, 1, ids.length));
        given(rs.getString("user_name")).willReturn(userNames[0], Arrays.copyOfRange(userNames, 1, userNames.length));
        given(rs.getString("first_name")).willReturn(firstNames[0], Arrays.copyOfRange(firstNames, 1, firstNames.length));
        given(rs.getString("last_name")).willReturn(lastNames[0], Arrays.copyOfRange(lastNames, 1, lastNames.length));
        given(rs.getString("role_name")).willReturn(roleNames[0], Arrays.copyOfRange(roleNames, 1, roleNames.length));

        return rs;
    }

}