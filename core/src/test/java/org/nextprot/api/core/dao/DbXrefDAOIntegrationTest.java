package org.nextprot.api.core.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.Interaction;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

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
	public void shouldReturn_1_XenoInteractantXref() {
 		Set<DbXref> xrefs = xrefdao.findEntryInteractionInteractantsXrefs("NX_A0JNW5");
 		assertTrue(xrefs.size()==1);
 		DbXref xref = xrefs.iterator().next();
 		xref.getAccession().equals("Q8ZAF0");
 		assertTrue(xref.getDatabaseCategory().equals("Sequence databases"));
 		assertTrue(xref.getDatabaseName().equals("UniProt"));
 		assertTrue(xref.getDbXrefId()==15645061L);
 		assertTrue(xref.getLinkUrl().equals("http://www.uniprot.org/uniprot/%s"));
 		assertTrue(xref.getResolvedUrl().equals("http://www.uniprot.org/uniprot/Q8ZAF0"));
 		assertTrue(xref.getUrl().equals("http://www.uniprot.org/uniprot/"));
 		//assertTrue(xref.getProperties().equals(""));
	}	
 		
	
	
}
