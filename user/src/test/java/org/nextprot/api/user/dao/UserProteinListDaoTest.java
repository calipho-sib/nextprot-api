package org.nextprot.api.user.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.nextprot.api.user.dao.test.base.UserResourceBaseTest;
import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@DatabaseSetup(value = "UserProteinListDaoTest.xml", type = DatabaseOperation.CLEAN_INSERT)
public class UserProteinListDaoTest extends UserResourceBaseTest {

	@Autowired private UserProteinListDao proteinListDao;

	private String username = "spongebob";

	@Test
	public void testGetUserProteinListByOwner() {

		List<UserProteinList> lists = proteinListDao.getUserProteinLists(username);

		assertNotNull(lists);
		assertTrue(!lists.isEmpty());
		assertEquals(1, lists.size());
		assertExpectedProteinList(lists.get(0), 156, "mylist", "my proteins", username, 23, 3, new HashSet<String>(), "ZZZZZU8V");
	}

	@Test
	public void testGetUserProteinListById() {

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertNotNull(list);
		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 3, Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), "ZZZZZU8V");
	}

    @Test
    public void testGetUserProteinListByPublicId() {

        UserProteinList list = proteinListDao.getUserProteinListByPublicId("ZZZZZU8V");

        assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 3, Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), "ZZZZZU8V");
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void testGetUserQueryByPublicIdNotFound() {

        proteinListDao.getUserProteinListByPublicId("00000005");
    }

	@Test
	public void testGetUserProteinListByName() {

		UserProteinList list = proteinListDao.getUserProteinListByName(username, "mylist");

		assertNotNull(list);
		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 3, Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), "ZZZZZU8V");
	}

	@Test (expected = EmptyResultDataAccessException.class)
	public void testGetUserProteinListByNameUnknownListName() {

		proteinListDao.getUserProteinListByName(username, "my list");
	}

	@Test (expected = EmptyResultDataAccessException.class)
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
        list.setPublicId("00000001");

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
        String publicId = "00000006";

        UserProteinList list = new UserProteinList();

        list.setName(name);
        list.setOwnerId(ownerId);
        list.setDescription(desc);
        list.setPublicId(publicId);

        long id = proteinListDao.createUserProteinList(list);

		list = proteinListDao.getUserProteinListById(id);

        assertNotNull(list);
        assertEquals(ownerId, list.getOwnerId());
        assertEquals(desc, list.getDescription());
        assertEquals(name, list.getName());
        assertEquals(publicId, list.getPublicId());
    }

	@Test(expected = DuplicateKeyException.class)
	public void testDuplicateUserProteinList() {

		UserProteinList l = new UserProteinList();
		l.setName("mylist");
		l.setOwnerId(23);
        l.setPublicId("00000001");

		proteinListDao.createUserProteinList(l);
	}

	@Test
	public void testCreateUserProteinListAccessions() {

		proteinListDao.createUserProteinListItems(156, Sets.newHashSet("prot1", "prot2"));

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 5,
				Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165", "prot1", "prot2"), "ZZZZZU8V");
	}

	@Test
	public void testCreateUserProteinListAccessions2() {

		Set<String> set = new HashSet<String>();
		for (int i=0 ; i<1000 ; i++) {

			set.add(String.valueOf(i));
		}

		proteinListDao.createUserProteinListItems(156, set);

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 1003,
				Sets.union(Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), set), "ZZZZZU8V");
	}

	@Test
	public void testUpdateUserProteinList() {

		UserProteinList l = new UserProteinList();

		l.setId(156);
		l.setName("ma liste");
		l.setDescription("la liste de bob leponge");

		proteinListDao.updateUserProteinListMetadata(l);

		UserProteinList l2 = proteinListDao.getUserProteinListById(l.getId());

		assertTrue(156 == l2.getId());
		assertEquals(l.getName(), l2.getName());
		assertEquals(l.getDescription(), l2.getDescription());
	}

	@Test
	public void testUpdateUserProteinList2() {

		UserProteinList l = new UserProteinList();

		l.setId(156);
		l.setName("ma liste");
		l.setDescription("la liste de bob leponge");
		l.setAccessions(Sets.newHashSet("NX_Q14249"));

		proteinListDao.updateUserProteinListMetadata(l);

		UserProteinList l2 = proteinListDao.getUserProteinListById(l.getId());

		assertExpectedProteinList(l2, 156, "ma liste", "la liste de bob leponge", username, 23, 3,
				Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), "ZZZZZU8V");
	}

	@Test
	public void testDeleteProteinListItems() {

		Set<String> accs = new HashSet<String>();
		accs.add("NX_Q14249");
		accs.add("NX_P05165");

		int count = proteinListDao.deleteProteinListItems(156, accs);

		assertEquals(2, count);

		UserProteinList list = proteinListDao.getUserProteinListById(156);

		assertExpectedProteinList(list, 156, "mylist", "my proteins", username, 23, 1,Sets.newHashSet("NX_Q8N5Z0"), "ZZZZZU8V");
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
				Sets.newHashSet("NX_Q14249", "NX_Q8N5Z0", "NX_P05165"), "ZZZZZU8V");
	}

	@Test
	public void testDeleteUserProteinList() {

		int count = proteinListDao.deleteUserProteinList(156);

		assertEquals(1, count);
		assertTrue(proteinListDao.getUserProteinLists("spongebob").isEmpty());
		assertEquals(new HashSet<String>(), proteinListDao.getAccessionsByListId(156));
	}

	@Test
	public void testDeleteUserProteinList2() {

		int count = proteinListDao.deleteUserProteinList(157);

		assertEquals(0, count);
		assertEquals(new HashSet<String>(), proteinListDao.getAccessionsByListId(157));
	}

    @Test(expected=DuplicateKeyException.class)
    public void testCreate2UserProteinListsWithDuplicatePublicIdFail() {

        UserProteinList list = new UserProteinList();

        list.setOwnerId(24);
        list.setPublicId("00000001");

        long id = proteinListDao.createUserProteinList(list);
        assertTrue(id > 0);

        list = new UserProteinList();

        list.setOwnerId(124);
        list.setPublicId("00000001");
        proteinListDao.createUserProteinList(list);
    }

	private static void assertExpectedProteinList(UserProteinList list, int expectedListId, String expectedListName, String expectedDescription,
												  String expectedOwner, int expectedOwnerId, int expectedProteinCount, Set<String> expectedProteins, String expectedPubid) {

		assertEquals(expectedListId, list.getId());
		assertEquals(expectedListName, list.getName());
		assertEquals(expectedDescription, list.getDescription());
		assertEquals(expectedOwner, list.getOwner());
		assertEquals(expectedOwnerId, list.getOwnerId());
		assertEquals(expectedProteinCount, list.getEntriesCount());
		assertEquals(expectedProteins, list.getAccessionNumbers());
        assertEquals(expectedPubid, list.getPublicId());
	}
}
