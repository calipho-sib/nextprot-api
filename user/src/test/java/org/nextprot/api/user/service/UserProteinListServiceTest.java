package org.nextprot.api.user.service;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.commons.exception.SearchQueryException;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.user.dao.UserProteinListDao;
import org.nextprot.api.user.domain.UserProteinList;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class UserProteinListServiceTest extends AbstractUnitBaseTest {

	// Check why the following annotation could be problematic !!!!!
	// http://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/
	@InjectMocks
	@Autowired
	private UserProteinListService proteinListService;

	@Mock
	private UserProteinListDao dao;

	@Mock
	private JWTCodec<Map<String, String>> codec;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateProteinList() {

		UserProteinList proteinList = mockSpongeBobProteinList("awesome", Sets.newHashSet("NX_P123"));

		when(dao.createUserProteinList(isA(UserProteinList.class))).thenReturn(10L);
		when(dao.getUserProteinListById(10L)).thenReturn(createUserProteinList(10L, "awesome", Sets.newHashSet("NX_P123")));

		UserProteinList created = proteinListService.createUserProteinList(proteinList);
		long id = created.getId();

		assertEquals(10, id);
		assertEquals("awesome", created.getName());
		assertEquals(Sets.newHashSet("NX_P123"), created.getAccessionNumbers());
	}

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
	public void testAddAccessions() {

		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		UserProteinList l1 = proteinListService.createUserProteinList(mockSpongeBobProteinList("cool1", s1));

		Set<String> accs = new HashSet<String>();
		this.proteinListService.addAccessionNumbers(l1.getId(), accs);
	}

	@Test
	public void testRemoveAccessions() {
		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");

		UserProteinList l1 = this.proteinListService.createUserProteinList(mockSpongeBobProteinList("cool1", s1));

		assertEquals("cool1", l1.getName());
		assertEquals(2, l1.getAccessionNumbers().size());

		Set<String> remAcc = new HashSet<String>();
		remAcc.add("NX_P123");
		this.proteinListService.removeAccessionNumbers(l1.getId(), remAcc);

		l1 = this.proteinListService.getUserProteinListById(l1.getId());
		assertEquals("cool1", l1.getName());
		assertEquals(1, l1.getAccessionNumbers().size());

	}

	@Test
	public void testCombine() {

		String TEST_USER = "asfas";

		Set<String> s1 = new HashSet<String>();
		s1.add("NX_P123");
		s1.add("NX_P456");
		UserProteinList l1 = this.proteinListService.createUserProteinList(mockSpongeBobProteinList("cool1", s1));

		Set<String> s2 = new HashSet<String>();
		s2.add("NX_P123");
		s2.add("NX_P321");
		UserProteinList l2 = this.proteinListService.createUserProteinList(mockSpongeBobProteinList("cool2", s2));

		UserProteinList l3 = this.proteinListService.combine("coolio", null, TEST_USER, l1.getName(), l2.getName(), UserProteinListService.Operations.OR);
		UserProteinList l4 = this.proteinListService.combine("homie", null, TEST_USER, l1.getName(), l2.getName(), UserProteinListService.Operations.AND);
		UserProteinList l5 = this.proteinListService.combine("rap", null, TEST_USER, l2.getName(), l1.getName(), UserProteinListService.Operations.NOT_IN);

		assertEquals("coolio", l3.getName());
		assertEquals(3, l3.getAccessionNumbers().size());

		assertEquals("homie", l4.getName());
		assertEquals(1, l4.getAccessionNumbers().size());
		assertEquals("NX_P123", l4.getAccessionNumbers().iterator().next());

		assertEquals("rap", l5.getName());
		assertEquals(1, l5.getAccessionNumbers().size());
		assertEquals("NX_P321", l5.getAccessionNumbers().iterator().next());
	}

	private static UserProteinList mockSpongeBobProteinList(String name, Set<String> accessions) {

		UserProteinList ul = Mockito.mock(UserProteinList.class);

		when(ul.getName()).thenReturn(name);
		when(ul.getAccessionNumbers()).thenReturn(accessions);
		when(ul.getOwnerName()).thenReturn("spongebob");

		return ul;
	}

	private UserProteinList createUserProteinList(long id, String name, Set<String> accessions) {

		UserProteinList ul = new UserProteinList();

		ul.setId(id);
		ul.setName(name);
		ul.setAccessions(accessions);

		return ul;
	}
}
