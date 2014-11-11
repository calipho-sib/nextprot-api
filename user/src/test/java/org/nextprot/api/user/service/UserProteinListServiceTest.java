package org.nextprot.api.user.service;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.dbunit.CommonsUnitBaseTest;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.commons.spring.jdbc.DataSourceServiceLocator;
import org.nextprot.api.user.domain.UserProteinList;
import org.nextprot.api.user.service.UserProteinListService.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Ignore
@ActiveProfiles("unit")
@DatabaseSetup(value = "UserProteinListServiceTest.xml", type = DatabaseOperation.INSERT)
public class UserProteinListServiceTest extends CommonsUnitBaseTest {
	@Autowired private DataSourceServiceLocator dsLocator;
	@Autowired private UserProteinListService proteinListService;

	private UserProteinList proteinList;
	
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
		UserProteinList created = this.proteinListService.createUserProteinList(proteinList);
		long id = created.getId();
		assertTrue(id ==  this.proteinListService.getUserProteinListById(id).getId());
	}
	
	@Test
	public void testCreateProteinList2() {
		Set<String> accs = new HashSet<String>();
		accs.add("NX_P123");
		
		UserProteinList l = this.proteinListService.createUserProteinList("awesome", null, accs, TEST_USER);
		assertEquals("awesome", l.getName());
	}
	
	@Ignore
	public void getProteinListSearchResult() throws SearchQueryException {
		//TODO rewrite this code on the solr module 
		/*Set<String> accs = new HashSet<String>();
		accs.add("NX_P01308");
		accs.add("NX_P06213");
		
		proteinList.setAccessions(accs);
		List<SearchResultItem> docs = this.solrService.getUserListSearchResult(proteinList).getResults();
		
		assertEquals(2, docs.size());
		assertEquals("NX_P01308", docs.get(0).getProperties().get("id"));
		assertEquals("NX_P06213", docs.get(1).getProperties().get("id"));*/
	}
	
	@Test
	public void testCombine() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		UserProteinList l1 = this.proteinListService.createUserProteinList("cool1", null, s1, TEST_USER);
				
		Set<String> s2 = new HashSet<String>();
		s2.add("NX_P123");
		s2.add("NX_P321");
		UserProteinList l2 = this.proteinListService.createUserProteinList("cool2", null, s2, TEST_USER);
		
		UserProteinList l3 = this.proteinListService.combine("coolio", null, TEST_USER, l1.getName(), l2.getName(), Operations.OR);
		UserProteinList l4 = this.proteinListService.combine("homie", null, TEST_USER, l1.getName(), l2.getName(), Operations.AND);
		UserProteinList l5 = this.proteinListService.combine("rap", null, TEST_USER, l2.getName(), l1.getName(), Operations.NOT_IN);
		
		assertEquals("coolio", l3.getName());
		assertEquals(3, l3.getAccessionNumbers().size());
		
		assertEquals("homie", l4.getName());
		assertEquals(1, l4.getAccessionNumbers().size());
		assertEquals("NX_P123", l4.getAccessionNumbers().iterator().next());
		
		assertEquals("rap", l5.getName());
		assertEquals(1, l5.getAccessionNumbers().size());
		assertEquals("NX_P321", l5.getAccessionNumbers().iterator().next());
	}
	
	
	@Test
	public void testAddAccessions()  {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		UserProteinList l1 = this.proteinListService.createUserProteinList("cool1", null, s1, TEST_USER);
		
		Set<String> accs = new HashSet<String>();
		this.proteinListService.addAccessionNumbers(l1.getId(), accs);
	}
	
	@Test
	public void testRemoveAccessions() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		UserProteinList l1 = this.proteinListService.createUserProteinList("cool1", null, s1, TEST_USER);
		
		assertEquals("cool1", l1.getName());
		assertEquals(2, l1.getAccessionNumbers().size());
		
		Set<String> remAcc = new HashSet<String>();
		remAcc.add("NX_P123");
		this.proteinListService.removeAccessionNumbers(l1.getId(), remAcc);
		
		l1 = this.proteinListService.getUserProteinListById(l1.getId());
		assertEquals("cool1", l1.getName());
		assertEquals(1, l1.getAccessionNumbers().size());
		
	}
}
