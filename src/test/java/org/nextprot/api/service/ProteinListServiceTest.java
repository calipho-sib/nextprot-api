package org.nextprot.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.core.domain.ProteinList;
import org.nextprot.api.core.service.ProteinListService;
import org.nextprot.api.core.service.ProteinListService.Operations;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.nextprot.api.solr.SearchResult.SearchResultItem;
import org.nextprot.auth.core.service.DataSourceServiceLocator;
import org.nextprot.auth.core.service.NextprotUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@Ignore
@ActiveProfiles("unit")
@DatabaseSetup(value = "ProteinListServiceTest.xml", type = DatabaseOperation.INSERT)
public class ProteinListServiceTest extends DBUnitBaseTest {
	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private ProteinListService proteinListService;
	@Autowired private NextprotUserService userService;
	
	private ProteinList proteinList;
	
	private static final String TEST_USER = "asfas";
	private static final String TEST_PASSWORD = "12212";
	
	public void setup() {
		
//		JdbcTemplate template = new JdbcTemplate(dsLocator.getDataSource());
//		template.update("insert into np_users.np_accessions values(1, 'NX_P123', 'cool')");
//		template.update("insert into np_users.np_accessions values(2, 'NX_P321', 'word')");
//		template.update("insert into np_users.np_accessions values(3, 'NX_P456', 'what?')");
//		
//		NextprotUser u = new NextprotUser(TEST_USER, TEST_PASSWORD);
//		u = this.userService.createUser(u);
//		
//		proteinList = new ProteinList();
//		proteinList.setOwnerId(u.getUserId());
//		proteinList.setName("wat?");
//		proteinList.setDescription("my first list");
	}
	
	@Test
	public void testCreateProteinList() {
		ProteinList created = this.proteinListService.createProteinList(proteinList);
		long id = created.getId();
		assertTrue(id ==  this.proteinListService.getProteinListById(id).getId());
	}
	
	@Test
	public void testCreateProteinList2() {
		Set<String> accs = new HashSet<String>();
		accs.add("NX_P123");
		
		ProteinList l = this.proteinListService.createProteinList("awesome", null, accs, TEST_USER);
		assertEquals("awesome", l.getName());
	}
	
	@Test
	public void getProteinListSearchResult() throws SearchQueryException {
		Set<String> accs = new HashSet<String>();
		accs.add("NX_P01308");
		accs.add("NX_P06213");
		
		proteinList.setAccessions(accs);
		List<SearchResultItem> docs = this.proteinListService.getProteinListSearchResult(proteinList).getResults();
		
		assertEquals(2, docs.size());
		assertEquals("NX_P01308", docs.get(0).getProperties().get("id"));
		assertEquals("NX_P06213", docs.get(1).getProperties().get("id"));
	}
	
	@Test
	public void testCombine() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		ProteinList l1 = this.proteinListService.createProteinList("cool1", null, s1, TEST_USER);
				
		Set<String> s2 = new HashSet<String>();
		s2.add("NX_P123");
		s2.add("NX_P321");
		ProteinList l2 = this.proteinListService.createProteinList("cool2", null, s2, TEST_USER);
		
		ProteinList l3 = this.proteinListService.combine("coolio", null, TEST_USER, l1.getName(), l2.getName(), Operations.OR);
		ProteinList l4 = this.proteinListService.combine("homie", null, TEST_USER, l1.getName(), l2.getName(), Operations.AND);
		ProteinList l5 = this.proteinListService.combine("rap", null, TEST_USER, l2.getName(), l1.getName(), Operations.NOT_IN);
		
		assertEquals("coolio", l3.getName());
		assertEquals(3, l3.getAccessions().size());
		
		assertEquals("homie", l4.getName());
		assertEquals(1, l4.getAccessions().size());
		assertEquals("NX_P123", l4.getAccessions().iterator().next());
		
		assertEquals("rap", l5.getName());
		assertEquals(1, l5.getAccessions().size());
		assertEquals("NX_P321", l5.getAccessions().iterator().next());
	}
	
	
	@Test
	public void testAddAccessions()  {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		ProteinList l1 = this.proteinListService.createProteinList("cool1", null, s1, TEST_USER);
		
		Set<String> accs = new HashSet<String>();
		this.proteinListService.addAccessions(l1.getId(), accs);
	}
	
	@Test
	public void testRemoveAccessions() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		ProteinList l1 = this.proteinListService.createProteinList("cool1", null, s1, TEST_USER);
		
		assertEquals("cool1", l1.getName());
		assertEquals(2, l1.getAccessions().size());
		
		Set<String> remAcc = new HashSet<String>();
		remAcc.add("NX_P123");
		this.proteinListService.removeAccessions(l1.getId(), remAcc);
		
		l1 = this.proteinListService.getProteinListById(l1.getId());
		assertEquals("cool1", l1.getName());
		assertEquals(1, l1.getAccessions().size());
		
	}
}
