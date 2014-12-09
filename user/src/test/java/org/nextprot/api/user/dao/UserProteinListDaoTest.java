package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.user.dao.test.base.UserApplicationBaseTest;
import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@DatabaseSetup(value = "UserProteinListDaoTest.xml", type = DatabaseOperation.INSERT)
public class UserProteinListDaoTest extends UserApplicationBaseTest {

	@Autowired private UserProteinListDao proteinListDao;

	private String username = "spongebob";

	@Test
	public void testGetUserProteinListByOwner() {

		List<UserProteinList> lists = proteinListDao.getUserProteinLists(username);

		assertNotNull(lists);
		assertTrue(!lists.isEmpty());
		assertEquals(1, lists.size());
		assertExpectedProteinList(lists.get(0), 156, "mylist", "my proteins", username, 23, 3, new HashSet<String>());
	}

	@Test
	public void testGetUserProteinListById() {

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertNotNull(list);
		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 3, Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"));
	}

	@Test
	public void testGetUserProteinListByName() {

		UserProteinList list = proteinListDao.getUserProteinListByName(username, "mylist");

		assertNotNull(list);
		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 3, Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"));
	}

	@Test (expected = NextProtException.class)
	public void testGetUserProteinListByNameUnknownListName() {

		proteinListDao.getUserProteinListByName(username, "my list");
	}

	@Test (expected = NextProtException.class)
	public void testGetUserProteinListByNameUnknownUserName() {

		proteinListDao.getUserProteinListByName("bobby", "mylist");
	}

	@Test
	public void testGetAccessionsByListId() {

		assertEquals(Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), proteinListDao.getAccessionsByListId(156));
	}

	@Test
	public void testGetAccessionsByListIdUnknownId() {

		assertEquals(new HashSet<String>(), proteinListDao.getAccessionsByListId(157));
	}

	@Test
    public void testCreateUserProteinList() {

        UserProteinList list = new UserProteinList();

        list.setOwnerId(24);

        long id = proteinListDao.createUserProteinList(list);
        assertTrue(id > 0);

        UserProteinList list2 = proteinListDao.getUserProteinListById(id);

        assertNotNull(list2);
        assertEquals(list.getOwnerId(), list2.getOwnerId());
		assertNull(list2.getDescription());
		assertNull(list2.getName());
    }

    @Test
    public void testCreateUserProteinList2() {

        int ownerId = 24;
        String desc = "my list";
        String name = "my list of proteins";

        UserProteinList list = new UserProteinList();

        list.setName(name);
        list.setOwnerId(ownerId);
        list.setDescription(desc);

        long id = proteinListDao.createUserProteinList(list);

		list = proteinListDao.getUserProteinListById(id);

        assertNotNull(list);
        assertEquals(ownerId, list.getOwnerId());
        assertEquals(desc, list.getDescription());
        assertEquals(name, list.getName());
    }

	@Test(expected = DuplicateKeyException.class)
	public void testDuplicateUserProteinList() {

		UserProteinList l = new UserProteinList();
		l.setName("mylist");
		l.setOwnerId(23);

		proteinListDao.createUserProteinList(l);
	}

	@Test
	public void testCreateUserProteinListAccessions() {

		proteinListDao.createUserProteinListAccessions(156, Sets.newHashSet("prot1", "prot2"));

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 5,
				Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165", "prot1", "prot2"));
	}

	@Test
	public void testCreateUserProteinListAccessions2() {

		Set<String> set = new HashSet<String>();
		for (int i=0 ; i<1000 ; i++) {

			set.add(String.valueOf(i));
		}

		proteinListDao.createUserProteinListAccessions(156, set);

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 1003,
				Sets.union(Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), set));
	}

	@Test
	public void testUpdateUserProteinList() {

		UserProteinList l = new UserProteinList();

		l.setId(156);
		l.setName("ma liste");
		l.setDescription("la liste de bob leponge");

		proteinListDao.updateUserProteinList(l);

		UserProteinList l2 = proteinListDao.getUserProteinListById(l.getId());

		assertTrue(156 == l2.getId());
		assertEquals(l.getName(), l2.getName());
		assertEquals(l.getDescription(), l2.getDescription());
	}

	@Test
	public void testDeleteProteinListItems() {

		Set<String> accs = new HashSet<String>();
		accs.add("NX_Q14249");
		accs.add("NX_P05165");

		int count = proteinListDao.deleteProteinListItems(156, accs);

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertEquals(2, count);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 1,
				Sets.newHashSet("NX_Q8N5Z0"));
	}

	@Test
	public void testDeleteProteinListItems2() {

		Set<String> accs = new HashSet<String>();
		accs.add("pim");
		accs.add("pam");
		accs.add("poum");

		int count = proteinListDao.deleteProteinListItems(156, accs);

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertEquals(0, count);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 3,
				Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"));
	}

	@Test
	public void testDeleteUserProteinList() {

		int count = proteinListDao.deleteUserProteinList(156);

		assertEquals(1, count);
		assertNull(proteinListDao.getUserProteinListById(156));
		assertEquals(new HashSet<String>(), proteinListDao.getAccessionsByListId(156));
	}

	@Test
	public void testDeleteUserProteinList2() {

		int count = proteinListDao.deleteUserProteinList(157);

		assertEquals(0, count);
		assertNull(proteinListDao.getUserProteinListById(157));
		assertEquals(new HashSet<String>(), proteinListDao.getAccessionsByListId(157));
	}

	private static void assertExpectedProteinList(UserProteinList list, int expectedListId, String expectedListName, String expectedDescription,
												  String expectedOwner, int expectedOwnerId, int expectedProteinCount, Set<String> expectedProteins) {

		assertEquals(expectedListId, list.getId());
		assertEquals(expectedListName, list.getName());
		assertEquals(expectedDescription, list.getDescription());
		assertEquals(expectedOwner, list.getOwner());
		assertEquals(expectedOwnerId, list.getOwnerId());
		assertEquals(expectedProteinCount, list.getEntriesCount());
		assertEquals(expectedProteins, list.getAccessionNumbers());
	}
}
