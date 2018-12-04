package org.nextprot.api.core.service.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.constants.PropertyApiModel;
import org.nextprot.api.core.domain.BioObject;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.Isoform;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.fluent.EntryConfig;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.nextprot.api.core.utils.IsoformUtils;
import org.nextprot.api.core.utils.seqmap.IsoformSequencePositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles({ "dev","cache"})
public class AnnotationUtilsTest extends CoreUnitBaseTest {

	@Autowired
	private EntryBuilderService entryBuilderService;
	@Autowired
	private AnnotationService annotationService;

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
		
		AnnotationUtils.convertRelativeEvidencesToProperties(annotations);

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
		
		AnnotationUtils.convertRelativeEvidencesToProperties(annotations);

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
    public void shouldcomputeIsoformsDisplayedAsSpecificForBinaryInteractionCase1()  { 

    	// BinaryInteraction with 2 isoforms, 1 specific flag => should return 1 isoformDiplayed as specific  
    	int isoCount=2;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso1",  spec1);
    	AnnotationIsoformSpecificity spec2=new AnnotationIsoformSpecificity();
    	spec2.setIsoformAccession("iso2");
    	spec2.setSpecificity("BY DEFAULT");
    	targetIsoformMap.put("iso2",  spec2);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.BINARY_INTERACTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(1, result.size());
        assertEquals("iso1", result.get(0));	
    }
    
    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForBinaryInteractionCase2()  { 

    	// BinaryInteraction with 2 isoforms, 2 specific flag => should return 0 isoformDiplayed as specific  
    	int isoCount=2;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso1",  spec1);
    	AnnotationIsoformSpecificity spec2=new AnnotationIsoformSpecificity();
    	spec2.setIsoformAccession("iso2");
    	spec2.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso2",  spec2);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.BINARY_INTERACTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(0, result.size());
    }
    
    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForBinaryInteractionCase3()  { 

    	// BinaryInteraction with 1 isoform, 1 specific flag => 0 isoformDiplayed as specific  
    	int isoCount=1;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso1",  spec1);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.BINARY_INTERACTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(0, result.size());

    }

    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForBinaryInteractionCase4()  { 

    	// BinaryInteraction with 1 isoform, 0 specific flag => 0 isoformDiplayed as specific  
    	int isoCount=1;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("BY DEFAULT");
    	targetIsoformMap.put("iso1",  spec1);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.BINARY_INTERACTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(0, result.size());	
    }
    
// --------------------------------
    
    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForNonBinaryInteractionCase1()  { 

    	// Non BinaryInteraction with 2 isoforms, 2 targetingIsoform records => should return 0 isoformDiplayed as specific  
    	int isoCount=2;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso1",  spec1);
    	AnnotationIsoformSpecificity spec2=new AnnotationIsoformSpecificity();
    	spec2.setIsoformAccession("iso2");
    	spec2.setSpecificity("BY DEFAULT");
    	targetIsoformMap.put("iso2",  spec2);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.GO_MOLECULAR_FUNCTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot, isoCount);
    	assertEquals(0, result.size());
    }
    
    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForNonBinaryInteractionCase2()  { 

    	// Non BinaryInteraction with 2 isoforms, 2 targetingIsoform records => should return 0 isoformDiplayed as specific   
    	int isoCount=2;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso1",  spec1);
    	AnnotationIsoformSpecificity spec2=new AnnotationIsoformSpecificity();
    	spec2.setIsoformAccession("iso2");
    	spec2.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso2",  spec2);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.GO_MOLECULAR_FUNCTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(0, result.size());
    }
    
    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForNonBinaryInteractionCase3()  { 

    	// BinaryInteraction with 1 isoform, 1 targetingIsoform record => should return 0 isoformDiplayed as specific   
    	int isoCount=1;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("SPECIFIC");
    	targetIsoformMap.put("iso1",  spec1);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.GO_MOLECULAR_FUNCTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(0, result.size());
 
    }

    @Test
    public void shouldcomputeIsoformsDisplayedAsSpecificForNonBinaryInteractionCase4()  { 

    	// BinaryInteraction with 2 isoforms, 1 targetingIsoform record => 1 isoformDiplayed as specific  
    	int isoCount=2;
    	Map<String, AnnotationIsoformSpecificity> targetIsoformMap = new HashMap<>();
    	AnnotationIsoformSpecificity spec1=new AnnotationIsoformSpecificity();
    	spec1.setIsoformAccession("iso1");
    	spec1.setSpecificity("BY DEFAULT");
    	targetIsoformMap.put("iso1",  spec1);
    	
    	Annotation annot = mock(Annotation.class);
    	when(annot.getAPICategory()).thenReturn(AnnotationCategory.GO_MOLECULAR_FUNCTION);
    	when(annot.getTargetingIsoformsMap()).thenReturn(targetIsoformMap);
    	
    	List<String> result = AnnotationUtils.computeIsoformsDisplayedAsSpecific(annot,isoCount);
    	assertEquals(1, result.size());	
    	assertEquals("iso1", result.get(0));
    }

    
// ----------------------------------    
    
    
    
    
	@Test
	public void testConvertEvidenceToExternalBioObject()  {

		AnnotationEvidence ev = new AnnotationEvidence();

		ev.setResourceAccession("CHEBI:38290");
		ev.setResourceAssociationType("relative");
		ev.setResourceDb("ChEBI");
		ev.setResourceId(39334228);

		BioObject bo = AnnotationUtils.newExternalChemicalBioObject(ev);
		Assert.assertEquals("CHEBI:38290", bo.getAccession());
		Assert.assertEquals("ChEBI", bo.getDatabase());
		Assert.assertEquals(39334228, bo.getId());
		Assert.assertEquals(BioObject.BioType.CHEMICAL, bo.getBioType());
	}

	//@Test
	public void exportMergedAnnotationsForBrca1AndScn9A() throws FileNotFoundException {

		List<String> accessions = Arrays.asList("NX_Q15858", "NX_P38398");

		List<String> headers = Arrays.asList("accession", "uniqueName", "category", "annotationName", "annotationHash", "masterPosition");

		PrintWriter pw = new PrintWriter("mergedBrca1AndScn9AVariants.tsv");

		// write header line
		pw.append(headers.stream().collect(Collectors.joining("\t"))).append("\n");

		for (String accession : accessions) {
			Entry entry = entryBuilderService.build(EntryConfig.newConfig(accession).withAnnotations());

			List<Annotation> mergedAnnotations = entry.getAnnotations().stream()
					.filter(a -> a.getAnnotationHash() != null)
					.filter(a -> a.getUniqueName().startsWith("AN"))
					.filter(a -> a.getAPICategory() == AnnotationCategory.VARIANT || a.getAPICategory() == AnnotationCategory.MUTAGENESIS)
					.collect(Collectors.toList());

			pw.append(exportAnnotationsAsTsvString(entry, mergedAnnotations));
		}

		pw.close();
	}

	@Test
	public void shouldFilterBindingTypeDescendantAnnotations() {

		List<Annotation> annotations = entryBuilderService.build(EntryConfig.newConfig("NX_P01308")
				.with("go-molecular-function")).getAnnotations();

		Assert.assertEquals(6, annotations.size());

		List<Annotation> filtered = annotations.stream()
				.filter(annotationService.createDescendantTermPredicate("GO:0005102"))
				.collect(Collectors.toList());

		Assert.assertEquals(3, filtered.size());
		Set<String> terms = filtered.stream().map(Annotation::getCvTermAccessionCode).collect(Collectors.toSet());

		Assert.assertTrue(terms.contains("GO:0005158"));
		Assert.assertTrue(terms.contains("GO:0005159"));
		Assert.assertTrue(terms.contains("GO:0005179"));
	}

	@Test
	public void shouldFilterByPropertyTopologyExistence() {

		List<Annotation> annotations = entryBuilderService.build(EntryConfig.newConfig("NX_P04083")
				.with("subcellular-location")).getAnnotations();

		Assert.assertEquals(22, annotations.size());

		List<Annotation> filtered = annotations.stream()
				.filter(annotationService.buildPropertyPredicate("topology", null))
				.collect(Collectors.toList());

		Assert.assertEquals(4, filtered.size());
		for (Annotation annot : filtered) {

			Assert.assertNotNull(annot.getPropertiesByKey("topology"));
		}
	}

	@Test
	public void shouldNotFilterByPropertyTopologyExistence() {

		List<Annotation> annotations = entryBuilderService.build(EntryConfig.newConfig("NX_P04083")
				.with("subcellular-location")).getAnnotations();

		List<Annotation> filtered = annotations.stream()
				.filter(annotationService.buildPropertyPredicate("tOpology", null))
				.collect(Collectors.toList());

		Assert.assertTrue(filtered.isEmpty());
	}

	@Test
	public void shouldFilterByPropertyTopologyValue() {

		List<Annotation> annotations = entryBuilderService.build(EntryConfig.newConfig("NX_P04083")
				.with("subcellular-location")).getAnnotations();

		List<Annotation> filtered = annotations.stream()
				.filter(annotationService.buildPropertyPredicate("topology", "Peripheral membrane protein"))
				.collect(Collectors.toList());

		for (Annotation annot : filtered) {

			for (AnnotationProperty property : annot.getPropertiesByKey("topology")) {

				Assert.assertEquals("Peripheral membrane protein", property.getValue());
			}
		}
	}

	@Test
	public void shouldNotFilterByPropertyTopologyValue() {

		List<Annotation> annotations = entryBuilderService.build(EntryConfig.newConfig("NX_P04083")
				.with("subcellular-location")).getAnnotations();

		List<Annotation> filtered = annotations.stream()
				.filter(annotationService.buildPropertyPredicate("topology", "Peripheral mEMbrane protein"))
				.collect(Collectors.toList());

		Assert.assertTrue(filtered.isEmpty());
	}

	@Test
	public void shouldFilterByPropertyTopologyAccession() {

		List<Annotation> annotations = entryBuilderService.build(EntryConfig.newConfig("NX_P04083")
				.with("subcellular-location")).getAnnotations();

		List<Annotation> filtered = annotations.stream()
				.filter(annotationService.buildPropertyPredicate("topology", "SL-9903"))
				.collect(Collectors.toList());

		Assert.assertTrue(!filtered.isEmpty());

		for (Annotation annot : filtered) {

			for (AnnotationProperty property : annot.getPropertiesByKey("topology")) {

				Assert.assertEquals("Peripheral membrane protein", property.getValue());
			}
		}
	}

	private String exportAnnotationsAsTsvString(Entry entry, List<Annotation> mergedAnnotations) {

		Isoform canonical = IsoformUtils.getCanonicalIsoform(entry);

		StringBuilder sb = new StringBuilder();

		for (Annotation annotation : mergedAnnotations) {

			List<String> row = Arrays.asList(
					entry.getUniqueName(),
					annotation.getUniqueName(),
					annotation.getAPICategory().getApiTypeName(),
					annotation.getAnnotationName(),
					annotation.getAnnotationHash(),
					String.valueOf(computeMasterPos(canonical, annotation.getTargetingIsoformsMap().get(canonical.getIsoformAccession()).getFirstPosition()))
			);

			// write annotation line
			sb.append(row.stream().collect(Collectors.joining("\t"))).append("\n");
		}

		return sb.toString();
	}

	private int computeMasterPos(Isoform canonical, Integer integer) {

		return IsoformSequencePositionMapper.getCodonPositionsOnMaster(integer, canonical).getNucleotidePosition(0);
	}

	public static void assertContainsExpectedProperties(Collection<AnnotationProperty> properties, AnnotationProperty... expectedProperties) {

		for (AnnotationProperty property : expectedProperties) {
			//System.out.println("expectedProperty:" + property.getName() + "found:" + properties.contains(property));
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
}
