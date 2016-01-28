package org.nextprot.api.core.dao;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.utils.ExpectedElementTester;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

		ExpectedDbXrefTester tester = new ExpectedDbXrefTester(xrefs);

		Map<String, Object> expectedProps = newExpectedProps("Q8ZAF0", "Sequence databases", "UniProt",
				"http://www.uniprot.org/uniprot/%s", "http://www.uniprot.org/uniprot/Q8ZAF0", "http://www.uniprot.org/uniprot/");
		Assert.assertTrue(tester.containsWithExpectedContent(15645061L, expectedProps));

		expectedProps = newExpectedProps("P61021", "Sequence databases", "UniProt",
				"http://www.uniprot.org/uniprot/%s", "http://www.uniprot.org/uniprot/P61021", "http://www.uniprot.org/uniprot/");
		Assert.assertTrue(tester.containsWithExpectedContent(29231790L, expectedProps));
	}

	private static Map<String, Object> newExpectedProps(String accession, String dbCat, String dbName, String linkUrl, String resolvedUrl, String url) {

		Map<String, Object> expectedProps = new HashMap<>();

		expectedProps.put("accession", accession);
		expectedProps.put("dbCat", dbCat);
		expectedProps.put("dbName", dbName);
		expectedProps.put("linkUrl", linkUrl);
		expectedProps.put("resolvedUrl", resolvedUrl);
		expectedProps.put("url", url);

		return expectedProps;
	}

	private static class ExpectedDbXrefTester extends ExpectedElementTester<DbXref, Long> {

		ExpectedDbXrefTester(Collection<DbXref> observedCollection) {
			super(observedCollection);
		}

		@Override
		protected Function<DbXref, Long> createElementToKeyFunc() {

			return new Function<DbXref, Long>() {
				@Override
				public Long apply(DbXref xref) {
					return xref.getDbXrefId();
				}
			};
		}

		@Override
		protected boolean hasExpectedContent(DbXref dbxref, Map<String, Object> expectedElementValues) {

			return expectedElementValues.get("accession").equals(dbxref.getAccession()) &&
					expectedElementValues.get("dbCat").equals(dbxref.getDatabaseCategory()) &&
				expectedElementValues.get("dbName").equals(dbxref.getDatabaseName()) &&
				expectedElementValues.get("linkUrl").equals(dbxref.getLinkUrl()) &&
				expectedElementValues.get("resolvedUrl").equals(dbxref.getResolvedUrl()) &&
				expectedElementValues.get("url").equals(dbxref.getUrl());
		}
	}
}
