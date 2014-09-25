package org.nextprot.api.core.controller.annotation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.nextprot.api.commons.dbunit.DBUnitBaseTest;
import org.nextprot.api.core.dao.AnnotationDAO;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationIsoformSpecificity;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author dteixeira
 */

@DatabaseSetup(value = "AnnotationMVCTest.xml", type = DatabaseOperation.INSERT)
public class AnnotationDaoTest extends DBUnitBaseTest {

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

}
