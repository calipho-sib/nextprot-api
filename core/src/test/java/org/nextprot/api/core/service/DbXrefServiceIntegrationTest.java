package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class DbXrefServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired
	private DbXrefService xrefService;

/*
 * This query finds entries having a single xref among 'Orphanet', 'KEGGPathway' , 'Reactome' and 'DrugBank'
 * It is convenient for tests: we know we get a single annotation from xrefs for a given entry
 * Example:
 * NX_A0AVF1 for Reactome
 * NX_A1L167 for Kegg
 * NX_A0PJY2 for Orphanet
 * NX_Q9Y2D1 for DrugBank

select a.unique_name, string_agg(a.acs, ',') as acs, string_agg(a.cv_name, ',') as dbs, count(*) as dbcount, sum(a.cnt) as xrcount from (
select si.unique_name, db.cv_name, count(*) as cnt, string_agg(x.accession, ',') as acs
from sequence_identifiers si
inner join identifier_resource_assoc ira on (si.identifier_id=ira.identifier_id)
inner join db_xrefs x on (ira.resource_id=x.resource_id)
inner join cv_databases db on (x.cv_database_id=db.cv_id)
where si.cv_type_id=1 and si.cv_status_id=1 
and db.cv_name in ('Orphanet', 'DrugBank','KEGGPathway','Reactome')
group by si.unique_name, db.cv_name
) a
group by a.unique_name
having sum(a.cnt)=1
;


 */
	
	@Test
	public void shouldReturn_1_ReactomeXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_A0AVF1");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationApiModel.PATHWAY.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()==AnnotationApiModel.PATHWAY);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("Uniprot"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("REACT_268024"));
		assertTrue(evi.getResourceDb().equals("Reactome"));

		assertEmptyProperties("NX_A0AVF1", 42610527);
	}
	
	@Test
	public void shouldReturn_1_KEGGPathwayXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_A1L167");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationApiModel.PATHWAY.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()==AnnotationApiModel.PATHWAY);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("NextProt"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("hsa04120+134111"));
		assertTrue(evi.getResourceDb().equals("KEGGPathway"));

		assertEmptyProperties("NX_A1L167", 14559832);
	}
	
	@Test
	public void shouldReturn_1_OrphanetXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_A0PJY2");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationApiModel.DISEASE.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()==AnnotationApiModel.DISEASE);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("Uniprot"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("478"));
		assertTrue(evi.getResourceDb().equals("Orphanet"));

		assertEmptyProperties("NX_A0PJY2", 1077769);
	}
	
/**
 * 	
 */
	
	@Test
	public void shouldReturn_1_DrugBankXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_Q9Y2D1");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationApiModel.SMALL_MOLECULE_INTERACTION.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()==AnnotationApiModel.SMALL_MOLECULE_INTERACTION);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("Uniprot"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("DB00852"));
		assertTrue(evi.getResourceDb().equals("DrugBank"));

		assertEmptyProperties("NX_Q9Y2D1", 983678);
	}
	

	private void assertEmptyProperties(String entryName, long propertyId) {

		List<DbXref> dbxrefs = this.xrefService.findDbXrefsByMaster(entryName);

		for (DbXref xref : dbxrefs)
			if (xref.getDbXrefId() == propertyId)
				Assert.assertTrue(xref.getProperties().isEmpty());
	}
	
}
