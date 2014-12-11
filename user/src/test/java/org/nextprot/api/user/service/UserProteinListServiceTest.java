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
		
		UserProteinList l = this.proteinListService.createUserProteinList(createUserProteinList("awesome", null, accs));
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
		UserProteinList l1 = this.proteinListService.createUserProteinList(createUserProteinList("cool1", null, s1));
				
		Set<String> s2 = new HashSet<String>();
		s2.add("NX_P123");
		s2.add("NX_P321");
		UserProteinList l2 = this.proteinListService.createUserProteinList(createUserProteinList("cool2", null, s2));
		
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
		UserProteinList l1 = this.proteinListService.createUserProteinList(createUserProteinList("cool1", null, s1));
		
		Set<String> accs = new HashSet<String>();
		//this.proteinListService.addAccessionNumbers(l1.getId(), accs);
	}
	
	@Test
	public void testRemoveAccessions() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		
		UserProteinList l1 = this.proteinListService.createUserProteinList(createUserProteinList("cool1", null, s1));
		
		assertEquals("cool1", l1.getName());
		assertEquals(2, l1.getAccessionNumbers().size());
		
		Set<String> remAcc = new HashSet<String>();
		remAcc.add("NX_P123");
		//this.proteinListService.removeAccessionNumbers(l1.getId(), remAcc);
		
		l1 = this.proteinListService.getUserProteinListById(l1.getId());
		assertEquals("cool1", l1.getName());
		assertEquals(1, l1.getAccessionNumbers().size());
		
	}
	
	private UserProteinList createUserProteinList(String name, String description, Set<String> accessions){
		
		UserProteinList ul = new UserProteinList();
		ul.setName(name);
		ul.setDescription(description);
		ul.setAccessions(accessions);
		ul.setOwner(TEST_USER);
		
		return ul;
		
	}
}
