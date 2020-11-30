package org.nextprot.api.core.dao;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.nextprot.api.commons.utils.CollectionTester;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
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
	
	/*
	 * Pam: 07.10.2020
	 * This test and related DAO method are absolete after we retrieve all interactions from NP2 pipeline
	 */
	@Ignore
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


    @Ignore
    @Test
    public void shouldFindSomeGenerifBackLinks() {
    	
        Map<String,String> map;
        
        // check we get at least one correct entry-link map entry
        map = xrefdao.getGeneRifBackLinks(48948592);
        Assert.assertTrue(map.size()>=1);
        Assert.assertEquals(
        		"http://europepmc.org/abstract/MED/27665733#sib-d197745eed8bdca966e29f3f6f57f1a5", 
        		map.get("NX_Q15116"));

        // check we get at least one correct entry-link map entry
        map = xrefdao.getGeneRifBackLinks(6908510);
        Assert.assertTrue(map.size()>1);
        Assert.assertTrue(map.containsKey("NX_P62633"));
        Assert.assertEquals(
        		"http://europepmc.org/abstract/MED/17672918#sib-262ccf729076a39de874ef544d6d4349", 
        		map.get("NX_Q8NHM5"));
        
        // check order of data returned on multiple links for each entry
        map = xrefdao.getGeneRifBackLinks(29155442);
        Assert.assertTrue(map.size()==2);
        Assert.assertEquals(
        		"http://europepmc.org/abstract/MED/23349856#sib-81b0ed8b9fd9e499203584651bd84010", 
        		map.get("NX_P68871"));
        Assert.assertEquals(
        		"http://europepmc.org/abstract/MED/23349856#sib-0a4c3ca3995e10f9a988b064490d6c73", 
        		map.get("NX_P69905"));
        
        

        
        
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
