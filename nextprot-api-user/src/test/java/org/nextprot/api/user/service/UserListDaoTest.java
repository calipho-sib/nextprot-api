package org.nextprot.api.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.user.dao.UserListDao;
import org.nextprot.api.user.domain.UserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

@Ignore
public class UserListDaoTest extends DBUnitBaseTest {

	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private UserListDao proteinListDao;
	
	private long listId;
	
	private String username;
	
//	public void setup() {
//		JdbcTemplate template = new JdbcTemplate(dsLocator.getDataSource());
//		template.update("insert into np_users.np_accessions values(1, 'NX_P123', 'cool')");		
//		template.update("insert into np_users.np_accessions values(2, 'NX_P321', 'bad')");
//
//		u = new NextprotUser("mario", "123");
//		u = this.userService.createUser(u);
//		
//	}
	
	@Test
	public void testGetAccessionsByListId() {
		Set<String> accs = new HashSet<String>();
		accs.add("NX_P123");
		
		UserList l = new UserList("awesome");
		l.setUsername(username);
		l.setAccessions(accs);
		
		listId = this.proteinListDao.saveProteinList(l);
//		Set<Long> ids = this.proteinListDao.getAccessionIds(accs);
		this.proteinListDao.saveProteinListAccessions(listId, accs);
		
		
		UserList pl = this.proteinListDao.getProteinListById(listId);
		assertNotNull(pl);
		
		pl.setAccessions(this.proteinListDao.getAccessionsByListId(listId));
		Set<String> s = pl.getAccessions();
		
		assertEquals(accs.size(), s.size());
		assertEquals(accs.iterator().next(), s.iterator().next());
	}
	
	@Test
	public void testSaveProteinList() {
		
		UserList l = new UserList("awesome");
		l.setUsername(username);
		
		long id = this.proteinListDao.saveProteinList(l);
		UserList l2 = this.proteinListDao.getProteinListById(id);
		
		assertNotNull(l2);
		assertEquals(l.getName(), l2.getName());
	}
	
	//TODO: add this in case of failure: 
	//TODO: create unique index protein_lists_unique_listname_user on "np_users"."protein_lists"(name,owner_id);
	@Test(expected = DuplicateKeyException.class)
	public void testSaveDuplicateProteinList() {
		UserList l1 = new UserList("awesome");
		l1.setUsername(username);
		UserList l2 = new UserList("awesome");
		l2.setUsername(username);		
		this.proteinListDao.saveProteinList(l1);
		this.proteinListDao.saveProteinList(l2);
	}
	
	@Test
	public void testUpdateProteinList() {
		UserList l = new UserList("awesome");
		l.setUsername(username);
		long id = this.proteinListDao.saveProteinList(l);
		l.setId(id);
		
		l.setName("coolio");
		l.setDescription("bla");
		this.proteinListDao.updateProteinList(l);
		
		UserList l2 = this.proteinListDao.getProteinListById(l.getId());
		
		assertTrue(id == l2.getId());
		assertEquals(l.getName(), l2.getName());
		assertEquals(l.getDescription(), l2.getDescription());
	}
	
	//TODO: FIX TEST
	@Ignore
	@Test
	public void testSaveProteinListAccessions() {
		Set<Long> accIds = new HashSet<Long>();
		accIds.add(1L);
		
		UserList l = new UserList("awesome");
		l.setUsername(username);
		long id = this.proteinListDao.saveProteinList(l);
		l.setId(id);
		
//		this.proteinListDao.saveProteinListAcscessions(l.getId(), accIds);
		
		Set<String> accs = this.proteinListDao.getAccessionsByListId(l.getId());
		
		assertEquals(1, accs.size());
		assertEquals("NX_P123", accs.iterator().next());
	}
	
	@Test
	public void testDeleteProteinList() {
		UserList l = new UserList("awesome");
		l.setUsername(username);
		long id = this.proteinListDao.saveProteinList(l);
		
		UserList t1 = this.proteinListDao.getProteinListById(id);
		assertNotNull(t1);
		this.proteinListDao.deleteProteinList(id);
		UserList t2 = this.proteinListDao.getProteinListById(id);
		assertNotNull(t2);
	}
	
	@Test
	public void testGetAccessionsIds() {
		
		Set<String> accs = new HashSet<String>();
		accs.add("NX_P123");
		accs.add("NX_P321");
		
		Set<Long> ids = this.proteinListDao.getAccessionIds(accs);
		assertEquals(2, ids.size());
		
		Iterator<Long> it = ids.iterator();
		assertTrue(1 == it.next());
		assertTrue(2 == it.next());
	}
	
	@Test
	public void testGetProteinListById() {
		UserList l = new UserList("awesome");
		l.setUsername(username);
		
		long id = this.proteinListDao.saveProteinList(l);
		UserList l2 = this.proteinListDao.getProteinListById(id);
		
		assertNotNull(l2);
		assertEquals(l.getName(), l2.getName());
	}
	
	@Test
	public void testGetProteinListByNameForUser() {
		UserList l = new UserList("awesome");
		l.setUsername(username);
		long id = this.proteinListDao.saveProteinList(l);
		l.setId(id);
		
		UserList l2 = this.proteinListDao.getProteinListByNameForUser(username, l.getName());
		
		assertNotNull(l2);
		assertEquals(l.getName(), l2.getName());
		assertEquals(l.getUsername(), l2.getUsername());
	}
	
	//TODO: FIX TEST
	@Ignore
	@Test
	public void testDeleteProteinListAccessions() {
		UserList l = new UserList("coolio");
		l.setUsername(username);
		long id = this.proteinListDao.saveProteinList(l);
		
		Set<String> accs = new HashSet<String>();
		accs.add("NX_P123");
		accs.add("NX_P321");
		
//		Set<Long> accIds = this.proteinListDao.getAccessionIds(accs);
		
//		this.proteinListDao.saveProteinListAccessions(id, accIds);
		
		
		this.proteinListDao.deleteProteinListAccessions(listId, accs);
		UserList pl = this.proteinListDao.getProteinListById(id);
		
		assertEquals("coolio", pl.getName());
		
		Set<String> savedAccs = this.proteinListDao.getAccessionsByListId(pl.getId());
		assertEquals(0, savedAccs.size());
		
		
		
	}
}
