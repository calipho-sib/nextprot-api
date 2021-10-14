package org.nextprot.api.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.BioObject.BioType;
import org.nextprot.api.core.domain.BioObject.ResourceType;
import org.nextprot.api.core.domain.DbXref;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.commons.constants.QualityQualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

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
	public void shouldFindOrCreateDbXrefId() throws Exception {
		long id;
		id = xrefService.findXrefId("IntAct", "EBI-2115799,EBI-21199571"); // in db
		Assert.assertEquals(55711538L, id);
		id = xrefService.findXrefId("IntAct", "EBI-2115799,EBI-11156432"); // NOT in db
		Assert.assertEquals(7066117270788987512L, id);
		id = xrefService.findXrefId("DECIPHER", "SCN1A");
		Assert.assertEquals(7371177140918988903L, id);
		
	}

	@Test
	public void shouldCreateDecipherXref() {
		DbXref x = xrefService.createDecipherXref("NX_P35498");
		Assert.assertEquals("SCN1A", x.getAccession());
		Assert.assertEquals("Polymorphism and mutation databases",x.getDatabaseCategory());
		Assert.assertEquals("DECIPHER", x.getDatabaseName());
		Assert.assertEquals("https://www.deciphergenomics.org/gene/%s/overview/clinical-info", x.getLinkUrl());
		Assert.assertEquals("https://www.deciphergenomics.org/gene/SCN1A/overview/clinical-info", x.getResolvedUrl());
		Assert.assertEquals("https://www.deciphergenomics.org", x.getUrl());
		Assert.assertEquals(new Long(7371177140918988903L), x.getDbXrefId());
	}
	
	@Test
	public void shouldUseDbXrefIdToDetermineIdentify()  {
		
		DbXref x1 = new DbXref();
		x1.setAccession("toto");
		x1.setDatabaseName("IntAct");
		x1.setDbXrefId(1234L);
		DbXref x2 = new DbXref();
		x2.setAccession("totoAsWell");
		x2.setDatabaseName("IntAct");
		x2.setDbXrefId(1234L);
		DbXref x3 = new DbXref();
		x3.setAccession("toto");
		x3.setDatabaseName("IntAct");
		x3.setDbXrefId(3333L);
		Set<DbXref> set = new HashSet<>();
		set.add(x1);
		set.add(x2);
		set.add(x3);
		Assert.assertEquals(2, set.size());
		for (DbXref x: set) System.out.println(x.getDbXrefId() + " " + x.getAccession() + " " + x.getDatabaseName());
				
	}

	
		
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
		assertTrue(annotations.size() > 0);
		
		int idx = -1;
		for (Annotation a : annotations) {
			idx++;
			if (a.getAPICategory()== AnnotationCategory.SMALL_MOLECULE_INTERACTION) break;
		}
		
		Annotation annot = annotations.get(idx);
		assertTrue(annot.getCategory().equals(AnnotationCategory.SMALL_MOLECULE_INTERACTION.getDbAnnotationTypeName()));
		assertTrue(annot.getQualityQualifier().equals("SILVER"));
		assertEquals(null,annot.getDescription());
		BioObject bo = annot.getBioObject();
		assertNotNull(bo);
		assertEquals("DB00852", bo.getAccession());
		assertEquals(BioType.CHEMICAL, bo.getBioType());
		assertEquals("DrugBank", bo.getDatabase());
		assertEquals(983678L, bo.getId());
		assertEquals("Pseudoephedrine", bo.getPropertyValue("chemical name"));
		assertEquals(ResourceType.EXTERNAL, bo.getResourceType());
		
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

				Assert.assertEquals("http://doua.prabi.fr/cgi-bin/acnuc-ac2tree?query=%u&db=HOGENOM6", xref.getLinkUrl());
				Assert.assertEquals("http://doua.prabi.fr/cgi-bin/acnuc-ac2tree?query=P01308&db=HOGENOM6", xref.getResolvedUrl());

                break;
			}
		}
	}

	@Test
	public void testBrendaTypeLinkHasUrlCorrectlyResolved() {

		List<DbXref> xrefs = this.xrefService.findDbXrefsByMaster("NX_Q9BXA6");

		for (DbXref xref : xrefs) {

			if (xref.getDbXrefId() == 964246) {

				Assert.assertEquals("https://www.brenda-enzymes.org/enzyme.php?ecno=%s&UniProtAcc=%u&OrganismID=%d", xref.getLinkUrl());
				Assert.assertEquals("https://www.brenda-enzymes.org/enzyme.php?ecno=2.7.11.1&UniProtAcc=Q9BXA6", xref.getResolvedUrl());
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
    public void shouldFindExistingXrefId()  {

        long id = xrefService.findXrefId("UniProt", "Q8WV60-1");
        Assert.assertEquals(1537966, id);
    }

    @Test(expected = Exception.class)
    public void shouldNotFindXrefIdMissingDb()  {

        xrefService.findXrefId("roudoudou", "Q8WV60-1");
    }

    @Test
    public void shouldGenerateNonExistingXrefId()  {

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
