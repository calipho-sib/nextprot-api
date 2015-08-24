package org.nextprot.api.core.service.impl;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationApiModel;
import org.nextprot.api.core.dao.PeptideMappingDao;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })

public class PeptideMappingServiceUnitTest extends CoreUnitBaseTest {
	
	//@Autowired private PeptideMappingService peptideMappingService;

	@Test
    public void shouldBuild_1_AnnotationWith_2_IsoSpecs()  {
		Long annotId = 3562876L;
		List<Map<String,Object>> records = new ArrayList<>();
		records.add(buildDaoRecord(annotId, "GOLD", 2, "NX_Q9UGM3-1", "NX_PEPT00113713", 881, 901));
		records.add(buildDaoRecord(annotId, "GOLD", 2, "NX_Q9UGM3-2", "NX_PEPT00113713", 382, 402));
    	Map<Long,Annotation> annotationMap = PeptideMappingServiceImpl.buildAnnotationMapFromRecords(records, true);
    	assertTrue(annotationMap.size()==1);
    	Annotation annot = annotationMap.get(annotId);
    	assertTrue(annot.getAnnotationId()==annotId);
    	assertTrue(annot.getAPICategory()==AnnotationApiModel.PEPTIDE_MAPPING);
    	assertTrue(annot.getCategory().equals(AnnotationApiModel.PEPTIDE_MAPPING.getDbAnnotationTypeName()));
    	AnnotationProperty prop = annot.getProperties().get(0);
    	assertTrue(prop.getAnnotationId()==annotId);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME));
    	assertTrue(prop.getValue().equals("NX_PEPT00113713"));
    	assertTrue(annot.getQualityQualifier().equals("GOLD"));
    	assertTrue(annot.getTargetingIsoformsMap().size()==2);
    	AnnotationIsoformSpecificity spec = annot.getTargetingIsoformsMap().get("NX_Q9UGM3-1");
    	assertTrue(spec.getAnnotationId()==annotId);
    	assertTrue(spec.getFirstPosition()==881);
    	assertTrue(spec.getLastPosition()==901);
    	spec.getIsoformName().equals("NX_Q9UGM3-1");
    	assertTrue(spec.getSpecificity().equals("SPECIFIC"));
    	spec = annot.getTargetingIsoformsMap().get("NX_Q9UGM3-2");
    	assertTrue(spec.getAnnotationId()==annotId);
    	assertTrue(spec.getFirstPosition()==382);
    	assertTrue(spec.getLastPosition()==402);
    	spec.getIsoformName().equals("NX_Q9UGM3-2");
    	assertTrue(spec.getSpecificity().equals("SPECIFIC"));
    	
    }
    
	@Test
    public void shouldBuild_2_AnnotationsEachHaving_1_IsoSpec()  {
		Long annotId = 3562876L;
		Long annotId2 = 3500000L;
		List<Map<String,Object>> records = new ArrayList<>();
		records.add(buildDaoRecord(annotId, "GOLD", 2, "NX_Q9UGM3-1", "NX_PEPT00113713", 881, 901));
		records.add(buildDaoRecord(annotId2, "GOLD", 3, "NX_Q9UGM3-1", "NX_PEPT00113713", 382, 402));

		Map<Long,Annotation> annotationMap = PeptideMappingServiceImpl.buildAnnotationMapFromRecords(records, true);
    	assertTrue(annotationMap.size()==2);

    	// checking first annotation
    	Annotation annot = annotationMap.get(annotId);
    	assertTrue(annot.getAnnotationId()==annotId);
    	assertTrue(annot.getAPICategory()==AnnotationApiModel.PEPTIDE_MAPPING);
    	assertTrue(annot.getCategory().equals(AnnotationApiModel.PEPTIDE_MAPPING.getDbAnnotationTypeName()));
    	AnnotationProperty prop = annot.getProperties().get(0);
    	assertTrue(prop.getAnnotationId()==annotId);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME));
    	assertTrue(prop.getValue().equals("NX_PEPT00113713"));
    	assertTrue(annot.getQualityQualifier().equals("GOLD"));
    	assertTrue(annot.getTargetingIsoformsMap().size()==1);
    	AnnotationIsoformSpecificity spec = annot.getTargetingIsoformsMap().get("NX_Q9UGM3-1");
    	assertTrue(spec.getAnnotationId()==annotId);
    	assertTrue(spec.getFirstPosition()==881);
    	assertTrue(spec.getLastPosition()==901);
    	spec.getIsoformName().equals("NX_Q9UGM3-1");
    	assertTrue(spec.getSpecificity().equals("SPECIFIC"));
    	
		// checking second annotation
    	annot = annotationMap.get(annotId2);
    	assertTrue(annot.getAnnotationId()==annotId2);
    	assertTrue(annot.getAPICategory()==AnnotationApiModel.PEPTIDE_MAPPING);
    	assertTrue(annot.getCategory().equals(AnnotationApiModel.PEPTIDE_MAPPING.getDbAnnotationTypeName()));
    	prop = annot.getProperties().get(0);
    	assertTrue(prop.getAnnotationId()==annotId2);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME));
    	assertTrue(prop.getValue().equals("NX_PEPT00113713"));
    	assertTrue(annot.getQualityQualifier().equals("GOLD"));
    	assertTrue(annot.getTargetingIsoformsMap().size()==1);
    	spec = annot.getTargetingIsoformsMap().get("NX_Q9UGM3-1");
    	assertTrue(spec.getAnnotationId()==annotId2);
    	assertTrue(spec.getFirstPosition()==382);
    	assertTrue(spec.getLastPosition()==402);
    	spec.getIsoformName().equals("NX_Q9UGM3-1");
    	assertTrue(spec.getSpecificity().equals("SPECIFIC"));
    	
    }

	@Test
    public void shouldBuild_2_AnnotationsAboutSamePeptide_EachAnnotationHaving_2_Properties()  {
		Long annotId = 3562876L;
		Long annotId2 = 3500000L;
		List<Map<String,Object>> records = new ArrayList<>();
		records.add(buildDaoRecord(annotId, "GOLD", 2, "NX_Q9UGM3-1", "NX_PEPT00113713", 881, 901));
		records.add(buildDaoRecord(annotId2, "GOLD", 3, "NX_Q9UGM3-1", "NX_PEPT00113713", 382, 402));

		List<AnnotationProperty> props = new ArrayList<>();
		//props.add(buildAnnotationProperty("NX_PEPT00113713", "peptide name", "NX_PEPT00113713"));
		props.add(buildAnnotationProperty("NX_PEPT00113713", "is proteotypic", "Y"));
		props.add(buildAnnotationProperty("NX_PEPT00113713", "is natural", "Y"));
		props.add(buildAnnotationProperty("NX_PEPT00113713", "is synthetic", "N"));
		Map<String,List<AnnotationProperty>> propMap = new HashMap<>();
		propMap.put("NX_PEPT00113713", props);
		
		Map<Long,Annotation> annotationMap = PeptideMappingServiceImpl.buildAnnotationMapFromRecords(records, true);
		List<Annotation> annotations = new ArrayList<Annotation>(annotationMap.values());
		PeptideMappingServiceImpl.attachPeptidePropertiesToAnnotations(annotations, propMap);
		
		assertTrue(annotationMap.size()==2);

    	Annotation annot;
    	AnnotationProperty prop;

    	// checking first annotation
    	annot = annotationMap.get(annotId);
    	assertTrue(annot.getAnnotationId()==annotId);

    	prop = annot.getProperties().get(0);
    	assertTrue(prop.getAnnotationId()==annotId);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME));
    	assertTrue(prop.getValue().equals("NX_PEPT00113713"));
    	
    	prop = annot.getProperties().get(1);
    	System.out.println("annotationid in prop:" + prop.getAnnotationId());
    	assertTrue(prop.getAnnotationId()==annotId);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_PROTEOTYPICITY));
    	assertTrue(prop.getValue().equals("Y"));
    	

    	// checking second annotation
    	annot = annotationMap.get(annotId2);
    	assertTrue(annot.getAnnotationId()==annotId2);

    	prop = annot.getProperties().get(0);
    	assertTrue(prop.getAnnotationId()==annotId2);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_NAME));
    	assertTrue(prop.getValue().equals("NX_PEPT00113713"));
    	
    	prop = annot.getProperties().get(1);
    	System.out.println("annotationid in prop:" + prop.getAnnotationId());
    	assertTrue(prop.getAnnotationId()==annotId2);
    	assertTrue(prop.getName().equals(AnnotationProperty.NAME_PEPTIDE_PROTEOTYPICITY));
    	assertTrue(prop.getValue().equals("Y"));
    }
	
	@Test
    public void shouldBuild_2_AnnotationsAboutSamePeptide_EachAnnotationHaving_1_Evidence()  {
		Long annotId = 3562876L;
		Long annotId2 = 3500000L;
		List<Map<String,Object>> records = new ArrayList<>();
		records.add(buildDaoRecord(annotId, "GOLD", 2, "NX_Q9UGM3-1", "NX_PEPT00113713", 881, 901));
		records.add(buildDaoRecord(annotId2, "GOLD", 3, "NX_Q9UGM3-1", "NX_PEPT00113713", 382, 402));

		List<AnnotationEvidence> evidences = new ArrayList<>();
		//props.add(buildAnnotationProperty("NX_PEPT00113713", "peptide name", "NX_PEPT00113713"));
		evidences.add(buildAnnotationEvidence());
		Map<String,List<AnnotationEvidence>> evMap = new HashMap<>();
		evMap.put("NX_PEPT00113713", evidences);
		
		Map<Long,Annotation> annotationMap = PeptideMappingServiceImpl.buildAnnotationMapFromRecords(records, true);
		List<Annotation> annotations = new ArrayList<Annotation>(annotationMap.values());
		PeptideMappingServiceImpl.attachPeptideEvidencesToAnnotations(annotations, evMap);
		
		assertTrue(annotationMap.size()==2);

    	Annotation annot;
    	AnnotationEvidence ev;

    	// checking first annotation
    	annot = annotationMap.get(annotId);
    	assertTrue(annot.getAnnotationId()==annotId);

    	ev = annot.getEvidences().get(0);
    	System.out.println("evidence 1 annotation id = " + ev.getAnnotationId());
    	assertTrue(ev.getAnnotationId()==annotId);

    	// checking second annotation
    	annot = annotationMap.get(annotId2);
    	assertTrue(annot.getAnnotationId()==annotId2);

    	ev = annot.getEvidences().get(0);
    	System.out.println("evidence 2 annotation id = " + ev.getAnnotationId());
    	assertTrue(ev.getAnnotationId()==annotId2);
    	
    }
	
	
	AnnotationProperty buildAnnotationProperty(String peptideName, String propertyName, String propertyValue) {
		AnnotationProperty prop = new AnnotationProperty();
		prop.setAccession(peptideName);
		prop.setName(propertyName);
		prop.setValue(propertyValue);
		return prop;
	}
	
	AnnotationEvidence buildAnnotationEvidence() {
		AnnotationEvidence ev = new AnnotationEvidence();
		ev.setAnnotationId(0);
		ev.setAssignedBy("some source");
		ev.setAssignmentMethod("some method");
		ev.setEvidenceCodeAC("some eco");
		ev.setEvidenceCodeName("some eo name");
		ev.setEvidenceId(234234L);
		ev.setExperimentalContextId(null);
		ev.setNegativeEvidence(false);
		ev.setProperties(new ArrayList<AnnotationEvidenceProperty>());
		ev.setPublicationMD5(null);
		ev.setQualifierType("some qualifier type");
		ev.setQualityQualifier("some quality qualifier");
		ev.setResourceAccession("some resource ac");
		ev.setResourceAssociationType("some resource assoc type");
		ev.setResourceDb("some resource db");
		ev.setResourceDescription(null);
		ev.setResourceId(8347578437L);
		ev.setResourceType("some resource type");
		return ev;
	}
	
    Map<String,Object> buildDaoRecord(Long annotId, String quality, Integer rank, String iso, String pep, Integer first, Integer last)  {

    	Map<String,Object> rec = new HashMap<>();
    	rec.put(PeptideMappingDao.KEY_ANNOTATION_ID, annotId);
    	rec.put(PeptideMappingDao.KEY_FIRST_POS, first);
    	rec.put(PeptideMappingDao.KEY_ISO_UNIQUE_NAME, iso);
    	rec.put(PeptideMappingDao.KEY_LAST_POS,last);
    	rec.put(PeptideMappingDao.KEY_PEP_UNIQUE_NAME, pep);
    	rec.put(PeptideMappingDao.KEY_QUALITY_QUALIFIER, quality);
    	rec.put(PeptideMappingDao.KEY_RANK,rank);
    	
    	return rec;
    }
    
    
}
