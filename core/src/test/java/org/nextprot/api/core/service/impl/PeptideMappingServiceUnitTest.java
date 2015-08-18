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
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ "dev" })

public class PeptideMappingServiceUnitTest extends CoreUnitBaseTest {
	
	//@Autowired private PeptideMappingService peptideMappingService;

	@Test
    public void shouldBuild1AnnotationWith2IsoSpecs()  {
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
    public void shouldBuild2AnnotationsEachHaving1IsoSpec()  {
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
