package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.commons.constants.QualityQualifier;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnnotationUtilsTest extends CoreUnitBaseTest {

    @Test
    public void shouldTurnSequenceCautionRelativeEvidenceIntoDifferingSequenceProperty()  {
				
    	long annotId=1234;
    	
		// create evidence (type=3)
		AnnotationEvidence ev3 = new AnnotationEvidence();
		ev3.setAnnotationId(annotId);
		ev3.setAssignedBy(null);
		ev3.setAssignmentMethod(null);
		ev3.setEvidenceCodeAC("ECO-000003");
		ev3.setEvidenceId(3000);
		ev3.setNegativeEvidence(false);
		ev3.setQualityQualifier("GOLD");
		ev3.setResourceAccession("AC-0003");
		ev3.setResourceAssociationType("evidence");
		ev3.setResourceDb("DB-0003");
		ev3.setResourceDescription("Resource descr 3");
		ev3.setResourceId(3333);
		ev3.setResourceType("database");

		// create relative info (type=2)
		AnnotationEvidence ev2 = new AnnotationEvidence();
		ev2.setAnnotationId(annotId);
		ev2.setAssignedBy(null);
		ev2.setAssignmentMethod(null);
		ev2.setEvidenceCodeAC(null);
		ev2.setEvidenceId(2000);
		ev2.setNegativeEvidence(false);
		ev2.setQualityQualifier("SILVER");
		ev2.setResourceAccession("AC-0002");
		ev2.setResourceAssociationType("relative");
		ev2.setResourceDb("DB-0002");
		ev2.setResourceDescription("Resource descr 2");
		ev2.setResourceId(2222);
		ev2.setResourceType("database");

		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(ev2);
		evidences.add(ev3);
		
    	// create an annotation
		Annotation annot = new Annotation();
		annot.setAnnotationId(annotId);
		annot.setUniqueName("some_sequence_caution_annotation");
		annot.setCategory(AnnotationCategory.SEQUENCE_CAUTION.getDbAnnotationTypeName());
		annot.setQualityQualifier("GOLD");
		annot.setEvidences(evidences);

		annot.setDescription("");  // will be modified by convertType2EvidencesToProperties()

		List<Annotation> annotations = new ArrayList<>();
		annotations.add(annot);
		
		AnnotationUtils.convertType2EvidencesToProperties(annotations);

		Assert.assertEquals(1, annotations.get(0).getEvidences().size());  // evidence type 2 should removed
		Assert.assertEquals(1, annotations.get(0).getProperties().size()); // property should be created (replaces evidence removed)

        assertContainsExpectedProperties(annotations.get(0).getProperties(),
                newAnnotationProperty(1234, "AC-0002", PropertyApiModel.NAME_DIFFERING_SEQUENCE, String.valueOf(ev2.getResourceId()), PropertyApiModel.VALUE_TYPE_RIF));
    }

    @Test
    public void shouldTurnDiseaseRelativeEvidenceIntoAlternativeDiseaseTermProperty()  {
				
    	long annotId=1234;
    	
		// create evidence (type=3)
		AnnotationEvidence ev3 = new AnnotationEvidence();
		ev3.setAnnotationId(annotId);
		ev3.setAssignedBy(null);
		ev3.setAssignmentMethod(null);
		ev3.setEvidenceCodeAC("ECO-000003");
		ev3.setEvidenceId(3000);
		ev3.setNegativeEvidence(false);
		ev3.setQualityQualifier("GOLD");
		ev3.setResourceAccession("AC-0003");
		ev3.setResourceAssociationType("evidence");
		ev3.setResourceDb("DB-0003");
		ev3.setResourceDescription("Resource descr 3");
		ev3.setResourceId(3333);
		ev3.setResourceType("database");

		// create relative info (type=2)
		AnnotationEvidence ev2 = new AnnotationEvidence();
		ev2.setAnnotationId(annotId);
		ev2.setAssignedBy(null);
		ev2.setAssignmentMethod(null);
		ev2.setEvidenceCodeAC(null);
		ev2.setEvidenceId(2000);
		ev2.setNegativeEvidence(false);
		ev2.setQualityQualifier("SILVER");
		ev2.setResourceAccession("AC-0002");
		ev2.setResourceAssociationType("relative");
		ev2.setResourceDb("DB-0002");
		ev2.setResourceDescription("Resource descr 2");
		ev2.setResourceId(2222);
		ev2.setResourceType("database");

		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(ev2);
		evidences.add(ev3);
		
    	// create an annotation
		Annotation annot = new Annotation();
		annot.setAnnotationId(annotId);
		annot.setUniqueName("some_disease_annotation");
		annot.setCategory(AnnotationCategory.DISEASE.getDbAnnotationTypeName());
		annot.setQualityQualifier("GOLD");
		annot.setEvidences(evidences);

		annot.setDescription("");  // will be modified by convertType2EvidencesToProperties()

		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(annot);
		
		AnnotationUtils.convertType2EvidencesToProperties(annotations);

		Assert.assertEquals(1, annotations.get(0).getEvidences().size());  // evidence type 2 should removed
		Assert.assertEquals(1, annotations.get(0).getProperties().size()); // property should be created (replaces evidence removed)

        assertContainsExpectedProperties(annotations.get(0).getProperties(),
                newAnnotationProperty(1234, "AC-0002", PropertyApiModel.NAME_ALTERNATIVE_DISEASE_TERM, String.valueOf(ev2.getResourceId()), PropertyApiModel.VALUE_TYPE_RIF));
    }
    
    @Test
    public void shouldReturnAnnotationIfContainedInTheRange()  {

    	String isoName = "iso-1";
    	Annotation a1 = mock(Annotation.class);
    	when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);
    	when(a1.getStartPositionForIsoform(isoName)).thenReturn(10);
    	when(a1.getEndPositionForIsoform(isoName)).thenReturn(12);
    	
    	List<Annotation> filteredAnnots = AnnotationUtils.filterAnnotationsBetweenPositions(10, 20, Arrays.asList(a1), isoName);
    	assertEquals(filteredAnnots.size(), 1);
    }


    @Test
    public void shouldNotReturnAnnotationIfOverlapsALittleBit()  { //This is used on the pepX logic, if you want to change the logic be careful (add a flag or change the method name for example), but keep the same logic for pepX

    	String isoName = "iso-1";
    	Annotation a1 = mock(Annotation.class);
    	when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);
    	when(a1.getStartPositionForIsoform(isoName)).thenReturn(5);
    	when(a1.getEndPositionForIsoform(isoName)).thenReturn(10);
    	
    	assertTrue(AnnotationUtils.filterAnnotationsBetweenPositions(10, 20, Arrays.asList(a1), isoName).isEmpty());
    }

    @Test
    public void shouldNotReturnAnnotationIfOusideTheRange()  { 
    	
    	String isoName = "iso-1";
    	Annotation a1 = mock(Annotation.class);
    	when(a1.isAnnotationPositionalForIsoform(isoName)).thenReturn(true);
    	when(a1.getStartPositionForIsoform(isoName)).thenReturn(5);
    	when(a1.getEndPositionForIsoform(isoName)).thenReturn(9);
    	
    	assertTrue(AnnotationUtils.filterAnnotationsBetweenPositions(10, 20, Arrays.asList(a1), isoName).isEmpty());
    }

	@Test
	public void testConvertEvidenceToExternalBioObject()  {

		AnnotationEvidence ev = new AnnotationEvidence();

		ev.setResourceAccession("CHEBI:38290");
		ev.setResourceAssociationType("relative");
		ev.setResourceDb("ChEBI");
		ev.setResourceId(39334228);

		BioObject bo = AnnotationUtils.newExternalBioObject(ev);
		Assert.assertEquals("CHEBI:38290", bo.getAccession());
		Assert.assertEquals("ChEBI", bo.getDatabase());
		Assert.assertEquals(39334228, bo.getId());
		Assert.assertEquals(BioObject.BioType.CHEMICAL, bo.getBioType());
	}

	@Test
	public void testMergeTwoIdenticalList()  {

		AnnotationEvidence evidence = new AnnotationEvidence();
		evidence.setQualityQualifier(QualityQualifier.GOLD.name());
		evidence.setEvidenceCodeAC("ECO:0000304");
		evidence.setEvidenceCodeName("traceable author statement used in manual assertion");
		evidence.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
		evidence.setAssignedBy("PINC");

		List<Annotation> srcList = Arrays.asList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
				Collections.singletonList(evidence), "ECO:0000304", "hash"));
		List<Annotation> destList = Arrays.asList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
				Collections.singletonList(evidence), "ECO:0000304"));

		AnnotationUtils.merge(srcList, destList);

		Assert.assertEquals(1, srcList.size());
		Assert.assertEquals(1, destList.size());
		Assert.assertEquals(1, destList.get(0).getEvidences().size());

		/*
		<annotation quality="GOLD" annotation-internal-id="$annotation.getAnnotationHash()">
			<cv-term accession="GO:0006814" terminology="go-biological-process-cv">sodium ion transport</cv-term>
			<description>
				<![CDATA[ sodium ion transport ]]>
			</description>
			<evidence-list>
				<evidence is-negative="false" resource-internal-ref="726510" resource-assoc-type="evidence" quality="GOLD" resource-type="publication" source-internal-ref="PINC">
					<cv-term accession="ECO:0000304" terminology="evidence-code-ontology-cv">
						traceable author statement used in manual assertion
					</cv-term>
				</evidence>
			</evidence-list>
			<target-isoform-list>
				<target-isoform accession="NX_Q15858-1" specificity="UNKNOWN"/>
				<target-isoform accession="NX_Q15858-2" specificity="UNKNOWN"/>
				<target-isoform accession="NX_Q15858-3" specificity="UNKNOWN"/>
			</target-isoform-list>
		</annotation>
		 */
	}

	@Test
	public void testMergeTwoSameListDifferentEvidence()  {

		AnnotationEvidence evidence1 = new AnnotationEvidence();
		evidence1.setQualityQualifier(QualityQualifier.GOLD.name());
		evidence1.setEvidenceCodeAC("ECO:0000304");
		evidence1.setEvidenceCodeName("traceable author statement used in manual assertion");
		evidence1.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
		evidence1.setAssignedBy("PINC");

		AnnotationEvidence evidence2 = new AnnotationEvidence();
		evidence1.setQualityQualifier(QualityQualifier.GOLD.name());
		evidence1.setEvidenceCodeAC("ECO:0000304");
		evidence1.setEvidenceCodeName("you can trust sponge bob");
		evidence1.setEvidenceCodeOntology("EvidenceCodeOntologyCv");
		evidence1.setAssignedBy("SPONGEBOB");

		List<Annotation> srcList = Collections.singletonList(mockAnnotationWithHash(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
				Collections.singletonList(evidence2), "ECO:0000304", "hash"));
		List<Annotation> destList = Collections.singletonList(mockAnnotation(AnnotationCategory.GO_BIOLOGICAL_PROCESS,
				Collections.singletonList(evidence1), "ECO:0000304"));

		AnnotationUtils.merge(srcList, destList);

		Assert.assertEquals(1, srcList.size());
		Assert.assertEquals(1, destList.size());
		Assert.assertEquals(2, destList.get(0).getEvidences().size());
	}

	public static void assertContainsExpectedProperties(Collection<AnnotationProperty> properties, AnnotationProperty... expectedProperties) {

		for (AnnotationProperty property : expectedProperties) {

			Assert.assertTrue(properties.contains(property));
		}
	}

	public static AnnotationProperty newAnnotationProperty(long annotationId, String accession, String name, String value, String valueType) {

		AnnotationProperty property = new AnnotationProperty();

		property.setAnnotationId(annotationId);
		property.setAccession(accession);
		property.setName(name);
		property.setValue(value);
        property.setValueType(valueType);

		return property;
	}

	private static Annotation mockAnnotation(AnnotationCategory cat, List<AnnotationEvidence> evidences, String cvCode) {

		Annotation annotation =new Annotation();

		annotation.setCategory(cat);
		annotation.setEvidences(evidences);
		annotation.setCvTermAccessionCode(cvCode);

		return annotation;
	}

	private static Annotation mockAnnotationWithHash(AnnotationCategory cat, List<AnnotationEvidence> evidences, String cvCode, String hash) {

		Annotation annotation = mockAnnotation(cat, evidences, cvCode);

		annotation.setAnnotationHash(hash);

		return annotation;
	}
}
