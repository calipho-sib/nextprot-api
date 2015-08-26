package org.nextprot.api.core.utils;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(ev2);
		evidences.add(ev3);
		
    	// create an annotation
		Annotation annot = new Annotation();
		annot.setAnnotationId(annotId);
		annot.setUniqueName("some_sequence_caution_annotation");
		annot.setCategory(AnnotationApiModel.SEQUENCE_CAUTION.getDbAnnotationTypeName());
		annot.setQualityQualifier("GOLD");
		annot.setEvidences(evidences);

		annot.setDescription("");  // will be modified by convertType2EvidencesToProperties()
		annot.setProperties(null); // will be modified by convertType2EvidencesToProperties()

		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(annot);
		
		AnnotationUtils.convertType2EvidencesToProperties(annotations);

		Assert.assertEquals(1, annotations.get(0).getEvidences().size());  // evidence type 2 should removed
		Assert.assertEquals(1, annotations.get(0).getProperties().size()); // property should be created (replaces evidence removed)
		AnnotationProperty p = annotations.get(0).getProperties().get(0);
		
		Assert.assertEquals("AC-0002", p.getAccession());  // used to build the annotation description
		Assert.assertEquals(1234, p.getAnnotationId());
		Assert.assertEquals(AnnotationProperty.NAME_DIFFERING_SEQUENCE, p.getName());
		Assert.assertEquals(String.valueOf(ev2.getResourceId()), p.getValue());
		Assert.assertEquals(AnnotationProperty.VALUE_TYPE_RIF, p.getValueType());
				 
		// <property property-name="differing sequence" value="2222" value-type="resource-internal-ref" accession="AC-0002"/>
		// <property property-name="interactant" value="16867031" value-type="resource-internal-ref" accession="Q77M19"/>
		
    }
    
    
    @Test
    public void shouldTurnCofactorRelativeEvidenceIntoCofactorProperty()  {
				
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
		annot.setUniqueName("some_cofactor_annotation");
		annot.setCategory(AnnotationApiModel.COFACTOR.getDbAnnotationTypeName());
		annot.setQualityQualifier("GOLD");
		annot.setEvidences(evidences);

		annot.setDescription("");  // will be modified by convertType2EvidencesToProperties()
		annot.setProperties(null); // will be modified by convertType2EvidencesToProperties()

		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(annot);
		
		AnnotationUtils.convertType2EvidencesToProperties(annotations);

		Assert.assertEquals(1, annotations.get(0).getEvidences().size());  // evidence type 2 should removed
		Assert.assertEquals(1, annotations.get(0).getProperties().size()); // property should be created (replaces evidence removed)
		AnnotationProperty p = annotations.get(0).getProperties().get(0);
		
		Assert.assertEquals("AC-0002", p.getAccession());  // used to build the annotation description
		Assert.assertEquals(1234, p.getAnnotationId());
		Assert.assertEquals(AnnotationProperty.NAME_COFACTOR, p.getName());
		Assert.assertEquals(String.valueOf(ev2.getResourceId()), p.getValue());
		Assert.assertEquals(AnnotationProperty.VALUE_TYPE_RIF, p.getValueType());
				 
		// <property property-name="differing sequence" value="2222" value-type="resource-internal-ref" accession="AC-0002"/>
		// <property property-name="interactant" value="16867031" value-type="resource-internal-ref" accession="Q77M19"/>
		
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
		annot.setCategory(AnnotationApiModel.DISEASE.getDbAnnotationTypeName());
		annot.setQualityQualifier("GOLD");
		annot.setEvidences(evidences);

		annot.setDescription("");  // will be modified by convertType2EvidencesToProperties()
		annot.setProperties(null); // will be modified by convertType2EvidencesToProperties()

		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(annot);
		
		AnnotationUtils.convertType2EvidencesToProperties(annotations);

		Assert.assertEquals(1, annotations.get(0).getEvidences().size());  // evidence type 2 should removed
		Assert.assertEquals(1, annotations.get(0).getProperties().size()); // property should be created (replaces evidence removed)
		AnnotationProperty p = annotations.get(0).getProperties().get(0);
		
		Assert.assertEquals("AC-0002", p.getAccession());  // used to build the annotation description
		Assert.assertEquals(1234, p.getAnnotationId());
		Assert.assertEquals(AnnotationProperty.NAME_ALTERNATIVE_DISEASE_TERM, p.getName());
		Assert.assertEquals(String.valueOf(ev2.getResourceId()), p.getValue());
		Assert.assertEquals(AnnotationProperty.VALUE_TYPE_RIF, p.getValueType());
				 
		// <property property-name="differing sequence" value="2222" value-type="resource-internal-ref" accession="AC-0002"/>
		// <property property-name="interactant" value="16867031" value-type="resource-internal-ref" accession="Q77M19"/>
		
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
		Assert.assertEquals(BioObject.Kind.CHEMICAL, bo.getKind());
	}
}
