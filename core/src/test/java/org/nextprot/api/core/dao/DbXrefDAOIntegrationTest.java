package org.nextprot.api.core.dao;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.utils.CollectionTester;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@ActiveProfiles({ "dev" })
public class DbXrefDAOIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private DbXrefDao xrefdao;

/*
 *
 * Use the following query to retrieve some entry having a single xeno interactant (= a single row for a given entry unique_name)
 * 

-- how to retrieve xrefs of xeno interactants ...
select si.unique_name, xr2.resource_id, db2.cv_name as database_name, db2.url as database_url, db2.link_url as database_link, dbc2.cv_name as database_category, xr2.accession
from nextprot.sequence_identifiers si
inner join nextprot.partnership_partner_assoc p1 on (si.db_xref_id=p1.db_xref_id)
inner join nextprot.partnerships inter on (inter.partnership_id=p1.partnership_id)
inner join nextprot.partnership_partner_assoc p2 on (inter.partnership_id=p2.partnership_id)
inner join nextprot.db_xrefs xr2 on (p2.db_xref_id=xr2.resource_id)
inner join nextprot.cv_databases db2 on (xr2.cv_database_id=db2.cv_id)
inner join nextprot.cv_database_categories dbc2 on (dbc2.cv_id=db2.cv_category_id) 
where p1.assoc_id!=p2.assoc_id and inter.is_xeno=true
order by si.unique_name, xr2.accession
limit 100 	

 */
	
	@Test
	public void shouldReturn_2_XenoInteractantXrefs() {

		Set<DbXref> xrefs = xrefdao.findEntryInteractionInteractantsXrefs("NX_A0JNW5");

		DbXrefCollectionTester tester = new DbXrefCollectionTester(xrefs);

		Assert.assertTrue(tester.contains(Arrays.asList(
				mockDbXref(15645061L, "Q8ZAF0", "Sequence databases", "UniProt",
					"http://www.uniprot.org/uniprot/%s", "http://www.uniprot.org/uniprot/Q8ZAF0", "http://www.uniprot.org/uniprot/"),
				mockDbXref(29231790L, "P61021", "Sequence databases", "UniProt",
					"http://www.uniprot.org/uniprot/%s", "http://www.uniprot.org/uniprot/P61021", "http://www.uniprot.org/uniprot/")
			)
		));
	}

    @Test
    public void shouldFindNextprotDb() {

        Optional<Integer> id = xrefdao.findDatabaseId("neXtProt");
        Assert.assertTrue(id.isPresent());
        Assert.assertEquals(175, id.get().intValue());
    }

    @Test
    public void shouldNotFindRoudoudouDb() {

        Optional<Integer> id = xrefdao.findDatabaseId("roudoudou");
        Assert.assertFalse(id.isPresent());
    }

    private static DbXref mockDbXref(long id, String accession, String dbCat, String dbName, String linkUrl, String resolvedUrl, String url) {

		DbXref dbxref = Mockito.mock(DbXref.class);

		Mockito.when(dbxref.getDbXrefId()).thenReturn(id);
		Mockito.when(dbxref.getAccession()).thenReturn(accession);
		Mockito.when(dbxref.getDatabaseCategory()).thenReturn(dbCat);
		Mockito.when(dbxref.getDatabaseName()).thenReturn(dbName);
		Mockito.when(dbxref.getLinkUrl()).thenReturn(linkUrl);
		Mockito.when(dbxref.getResolvedUrl()).thenReturn(resolvedUrl);
		Mockito.when(dbxref.getUrl()).thenReturn(url);

		return dbxref;
	}

	private static class DbXrefCollectionTester extends CollectionTester<DbXref, Long> {

		DbXrefCollectionTester(Collection<DbXref> observedCollection) {
			super(observedCollection);
		}

		@Override
		protected Function<DbXref, Long> createElementToKeyFunc() {

			return xref -> xref.getDbXrefId();
		}

		@Override
		protected boolean isEquals(DbXref dbxref, DbXref expectedElement) {

			return expectedElement.getAccession().equals(dbxref.getAccession()) &&
					expectedElement.getDatabaseCategory().equals(dbxref.getDatabaseCategory()) &&
					expectedElement.getDatabaseName().equals(dbxref.getDatabaseName()) &&
					expectedElement.getLinkUrl().equals(dbxref.getLinkUrl()) &&
					expectedElement.getResolvedUrl().equals(dbxref.getResolvedUrl()) &&
					expectedElement.getUrl().equals(dbxref.getUrl());
		}
	}
}
