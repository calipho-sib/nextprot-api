package org.nextprot.api.controller.annotation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.dbunit.DBUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

/**
 * @author dteixeira
 */

@DatabaseSetup(value = "AnnotationMVCTest.xml", type = DatabaseOperation.INSERT)
public class AnnotationServiceTest extends DBUnitBaseTest {

	@Autowired
	AnnotationService annotationService;

	@Test
	public void shouldGetTheListOfAnnotationsFromService() {

		List<Annotation> annotations = annotationService.findAnnotations("NX_P12345");
		assertEquals(annotations.size(), 5);

		for (Annotation a : annotations) {
			System.out.println(a.getCategory());
		}

		System.out.println(annotations.iterator().next().getEvidences().size());
		System.out.println(annotations.iterator().next().getTargetingIsoformsMap().size());
		System.out.println(annotations.iterator().next().getProperties().size());

	}

}
