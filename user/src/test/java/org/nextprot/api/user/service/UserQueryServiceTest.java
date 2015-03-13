package org.nextprot.api.user.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.dbunit.AbstractUnitBaseTest;
import org.nextprot.api.commons.utils.StringGenService;
import org.nextprot.api.security.service.JWTCodec;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UserQueryServiceTest extends AbstractUnitBaseTest {

	// Check why the following annotation could be problematic !!!!!
	// http://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/
	@InjectMocks
	@Autowired
	private UserQueryService userQueryService;

	@Mock
	private UserQueryDao dao;

	@Mock
	private JWTCodec<Map<String, String>> codec;

    @Mock
    private StringGenService stringGenerator;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);
    }

	@Test
	public void testCreateUserQuery() {

		final UserQuery userQuery = createUserQuery("ma requete", "une simple requete", "yet another sparql query", true);

        dressMockedUserQueryDao(userQuery, 10);

        UserQuery created = userQueryService.createUserQuery(userQuery);

        assertEquals(10, created.getUserQueryId());
		assertEquals("ma requete", created.getTitle());

        Mockito.verify(stringGenerator, times(1)).generateString();
	}

    @Test
    public void testGeneratePubIdInCreateUserQuery() {

        UserQuery userQuery = createUserQuery("ma requete", "une simple requete", "yet another sparql query", true);

        Mockito.when(dao.createUserQuery(userQuery))
                .thenThrow(new DuplicateKeyException("ERROR: duplicate key value violates unique constraint \"user_queries_pubid_udx\"\n" +
                        "  Detail: Key (public_id)=(00000002) already exists."))
                .thenThrow(new DuplicateKeyException("ERROR: duplicate key value violates unique constraint \"user_queries_pubid_udx\"\n" +
                        "  Detail: Key (public_id)=(00000002) already exists."))
                .thenReturn(1L);

        userQueryService.createUserQuery(userQuery);

        Mockito.verify(stringGenerator, times(3)).generateString();
    }

    public static UserQuery createUserQuery(String title, String desc, String sparql, boolean published) {

        UserQuery query = new UserQuery();

        query.setTitle(title);
        query.setDescription(desc);
        query.setSparql(sparql);
        query.setPublished(published);

        return query;
    }

    private void dressMockedUserQueryDao(final UserQuery query, final long queryId) {

        when(dao.createUserQuery(query)).thenReturn(queryId);
        when(dao.getUserQueryById(queryId)).thenReturn(query);
    }
}
