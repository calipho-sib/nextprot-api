package org.nextprot.api.core.service;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.service.impl.DbXrefServiceImpl;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@ActiveProfiles({ "dev" })
public class DbXrefServiceIntegrationTest extends CoreUnitBaseTest {

	@Autowired private DbXrefService xrefService;
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
		assertTrue(annot.getCategory().equals(AnnotationCategory.PATHWAY.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()== AnnotationCategory.PATHWAY);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		Assert.assertEquals("Intraflagellar transport", annot.getDescription());
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("Reactome"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("R-HSA-5620924"));
		assertTrue(evi.getResourceDb().equals("Reactome"));

		Assert.assertTrue(annotations.get(0).getProperties().isEmpty());
	}
	
	@Test
	public void shouldReturn_1_KEGGPathwayXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_A1L167");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationCategory.PATHWAY.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()== AnnotationCategory.PATHWAY);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		Assert.assertEquals("Ubiquitin mediated proteolysis", annot.getDescription());
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("KEGG_PTW"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("hsa04120+134111"));
		assertTrue(evi.getResourceDb().equals("KEGGPathway"));

		Assert.assertTrue(annotations.get(0).getProperties().isEmpty());
	}
	
	@Test
	public void shouldReturn_1_OrphanetXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_A0PJY2");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationCategory.DISEASE.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()== AnnotationCategory.DISEASE);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		Assert.assertEquals("Kallmann syndrome", annot.getDescription());
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("Orphanet"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("478"));
		assertTrue(evi.getResourceDb().equals("Orphanet"));

		Assert.assertTrue(annotations.get(0).getProperties().isEmpty());
	}
	
	@Test
	public void shouldReturn_1_DrugBankXrefAsAnnotation() {
		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_Q9Y2D1");
		assertTrue(annotations.size() == 1);
		Annotation annot = annotations.get(0);
		assertTrue(annot.getCategory().equals(AnnotationCategory.SMALL_MOLECULE_INTERACTION.getDbAnnotationTypeName()));
		assertTrue(annot.getAPICategory()== AnnotationCategory.SMALL_MOLECULE_INTERACTION);
		assertTrue(annot.getQualityQualifier().equals("GOLD"));
		Assert.assertEquals("Pseudoephedrine", annot.getDescription());
		for (AnnotationIsoformSpecificity spec: annot.getTargetingIsoformsMap().values()) {
			assertTrue(spec.getSpecificity().equals("UNKNOWN"));
		}
		assertTrue(annot.getEvidences().size()==1);
		AnnotationEvidence evi = annot.getEvidences().get(0);
		assertTrue(evi.getAssignedBy().equals("DrugBank"));
		assertTrue(evi.getEvidenceCodeAC().equals("ECO:0000305"));
		assertTrue(evi.getResourceAccession().equals("DB00852"));
		assertTrue(evi.getResourceDb().equals("DrugBank"));

		Assert.assertTrue(annotations.get(0).getProperties().isEmpty());
	}

	@Test
	public void reactomeXrefShouldHaveEmptyProperties() {

		assertEmptyProperties("NX_A0AVF1", 42610527);
	}

	@Test
	public void KEGGPathwayXrefShouldHaveEmptyProperties() {

		assertEmptyProperties("NX_A1L167", 14559832);
	}

	@Test
	public void orphanetXrefShouldHaveEmptyProperties() {

		assertEmptyProperties("NX_A0PJY2", 1077769);
	}

	@Test
	public void drugBankXrefShouldHaveEmptyProperties() {

		assertEmptyProperties("NX_Q9Y2D1", 983678);
	}

    @Test
    public void testPercentSignSTypeLinkHasUrlCorrectlyResolved() {

        List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster("NX_P01308");

        for (DbXref xref : xrefs) {

            if (xref.getDbXrefId() == 1272250) {

                Assert.assertEquals("https://www.ncbi.nlm.nih.gov/protein/%s", xref.getLinkUrl());
                Assert.assertEquals("https://www.ncbi.nlm.nih.gov/protein/NP_000198.1", xref.getResolvedUrl());

                break;
            }
        }
    }

	@Test
	public void testPercentSignUTypeLinkHasUrlCorrectlyResolved() {

		List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster("NX_P01308");

		for (DbXref xref : xrefs) {

			if (xref.getDbXrefId() == 16387756) {

				Assert.assertEquals("http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=%u&db=HOGENOM", xref.getLinkUrl());
				Assert.assertEquals("http://pbil.univ-lyon1.fr/cgi-bin/acnuc-ac2tree?query=P01308&db=HOGENOM", xref.getResolvedUrl());

                break;
			}
		}
	}

	@Test
	public void testBrendaTypeLinkHasUrlCorrectlyResolved() {

		List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster("NX_Q9BXA6");

		for (DbXref xref : xrefs) {

			if (xref.getDbXrefId() == 964246) {

				Assert.assertEquals("http://www.brenda-enzymes.org/enzyme.php?ecno=%s&UniProtAcc=%u&OrganismID=%d", xref.getLinkUrl());
				Assert.assertEquals("http://www.brenda-enzymes.org/enzyme.php?ecno=2.7.11.1&UniProtAcc=Q9BXA6", xref.getResolvedUrl());

                break;
			}
		}
	}

	@Test
	public void shouldFindTransportActivityAnnotation() {

		List<Annotation> annotations = this.xrefService.findDbXrefsAsAnnotationsByEntry("NX_Q86VW1").stream()
				.filter(a -> a.getAPICategory() == AnnotationCategory.TRANSPORT_ACTIVITY)
				.collect(Collectors.toList());

		Assert.assertEquals(1, annotations.size());
		Annotation annotation = annotations.get(0);

		Assert.assertEquals("AN_Q86VW1_XR_6580312", annotation.getUniqueName());
		Assert.assertEquals("the major facilitator superfamily (mfs)", annotation.getDescription());

		List<AnnotationEvidence> evidences = annotation.getEvidences().stream()
				.filter(e -> e.getResourceDb().equals("TCDB"))
				.collect(Collectors.toList());

		// Assert Evidence
		Assert.assertEquals(1, evidences.size());
		AnnotationEvidence evidence = evidences.get(0);

		Assert.assertEquals("database", evidence.getResourceType());
		Assert.assertEquals("2.A.1.19.12", evidence.getResourceAccession());
		Assert.assertEquals("ECO:0000305", evidence.getEvidenceCodeAC());
		Assert.assertEquals(QualityQualifier.GOLD.toString(), evidence.getQualityQualifier());

		// Assert Xref
		Assert.assertNotNull(annotation.getParentXref());

		Assert.assertEquals("2.A.1.19.12", annotation.getParentXref().getAccession());
		Assert.assertEquals("TCDB", annotation.getParentXref().getDatabaseName());
		Assert.assertNotNull(annotation.getParentXref().getProperties());

		List<DbXref.DbXrefProperty> props = annotation.getParentXref().getProperties().stream()
				.filter(p -> p.getName().equals("family name"))
				.collect(Collectors.toList());

		Assert.assertEquals(1, props.size());
		Assert.assertEquals("the major facilitator superfamily (mfs)", props.get(0).getValue());
	}

    @Test
    public void shouldFindExistingXrefId() throws DbXrefServiceImpl.MissingCvDatabaseException {

        long id = xrefService.findXrefId("UniProt", "Q8WV60-1");
        Assert.assertEquals(1537966, id);
    }

    @Test(expected = DbXrefServiceImpl.MissingCvDatabaseException.class)
    public void shouldNotFindXrefIdMissingDb() throws DbXrefServiceImpl.MissingCvDatabaseException {

        xrefService.findXrefId("roudoudou", "Q8WV60-1");
    }

    @Test
    public void shouldGenerateNonExistingXrefId() throws DbXrefServiceImpl.MissingCvDatabaseException {

        long id = xrefService.findXrefId("UniProt", "Q8WV60-4");
        Assert.assertEquals(7143053370951092528L, id);
    }

	private void assertEmptyProperties(String entryName, long propertyId) {

		List<DbXref> dbxrefs = this.xrefService.findDbXrefsByMaster(entryName);

		for (DbXref xref : dbxrefs)
			if (xref.getDbXrefId() == propertyId)
				Assert.assertTrue(xref.getProperties().isEmpty());
	}
}
