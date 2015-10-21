package org.nextprot.api.core.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author dteixeira
 */

@DatabaseSetup(value = "AnnotationMVCTest.xml", type = DatabaseOperation.INSERT)
public class AnnotationDaoTest extends CoreUnitBaseTest {

	@Autowired AnnotationDAO annotationDAO;

	@Test
	public void shouldGetTheListOfAnnotations() {

		List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName("NX_P12345");
		assertEquals(annotations.size(), 5);

		long id = annotations.iterator().next().getAnnotationId();
		List<Long> ids = Arrays.asList(id);

		List<AnnotationEvidence> evidences = annotationDAO.findAnnotationEvidencesByAnnotationIds(ids);
		List<AnnotationIsoformSpecificity> isoforms = annotationDAO.findAnnotationIsoformsByAnnotationIds(ids);
		List<AnnotationProperty> property = annotationDAO.findAnnotationPropertiesByAnnotationIds(ids);

	}
	
	@Test
	public void shouldTurnBiotechnologyAnnotationToMiscellaneous() {
		List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName("NX_O43474");
		assertEquals(annotations.get(0).getCategory(), AnnotationCategory.MISCELLANEOUS.getDbAnnotationTypeName());
		assertEquals(annotations.size(),1);		
	}
	
	@Test
	public void shouldTurnTransmembraneAnnotationToIntramembrane() {
		List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName("NX_P51797");
		assertEquals(annotations.get(0).getCategory(), AnnotationCategory.INTRAMEMBRANE_REGION.getDbAnnotationTypeName());
		assertEquals(annotations.size(),1);		
	}
		
	@Test
	public void shouldSplitTransitPeptideAnnotations()  {
		List<Annotation> annotations = annotationDAO.findAnnotationsByEntryName("NX_Q6P4F2");
		List<String> list = new ArrayList<String>();
		for (Annotation an: annotations) list.add(an.getCategory());
		assertEquals(list.contains(AnnotationCategory.MITOCHONDRIAL_TRANSIT_PEPTIDE.getDbAnnotationTypeName()), true);
		assertEquals(list.contains(AnnotationCategory.PEROXISOME_TRANSIT_PEPTIDE.getDbAnnotationTypeName()), true);
		assertEquals(annotations.size(),2);		
	}
	
	
}
