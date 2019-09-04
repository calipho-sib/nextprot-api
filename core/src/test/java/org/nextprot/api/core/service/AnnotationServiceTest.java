package org.nextprot.api.core.service;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationProperty;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author pam
 */


@ActiveProfiles({"unit", "unit-schema-nextprot"})
@DatabaseSetup(value="AnnotationMVCTest.xml", type = DatabaseOperation.CLEAN_INSERT)
public class AnnotationServiceTest extends CoreUnitBaseTest {

	
	@Autowired AnnotationService annotationService;

	@Test
	public void shouldGetAnOrphanetAnnotationFromService() {

		List<Annotation> annotations = annotationService.findAnnotationsExcludingBed("NX_P10000");
		assertEquals(1,annotations.size());
		Annotation a = annotations.get(0);
		assertEquals("disease",a.getCategory());
		assertEquals("Some XR_ORPHA_100021 xref disease property value", a.getDescription());
		assertEquals("GOLD", a.getQualityQualifier());
		assertEquals(1, a.getEvidences().size());
		AnnotationEvidence evi = a.getEvidences().get(0);
		assertEquals("Orphanet", evi.getAssignedBy());
		assertEquals("curated", evi.getAssignmentMethod());
		assertEquals("IC", evi.getQualifierType());
		assertEquals(false, evi.isNegativeEvidence());
		assertEquals(true, evi.isValid());
		assertEquals("XR_ORPHA_100021", evi.getResourceAccession());
		assertEquals("Orphanet", evi.getResourceDb());
	}

	@Ignore
	@Test
	public void shouldAddVariantFrequenciesToVariantAnnotations() {
		List<Annotation> annotations = annotationService.findAnnotationsExcludingBed("NX_P20000");
		assertEquals(1, annotations.size());
		Annotation annotationWithGnomadVariants = annotations.get(0);
		AnnotationEvidence evidence = annotationWithGnomadVariants.getEvidences()
				.stream()
				.filter((annotationEvidence -> annotationEvidence.getResourceDb().equals("gnomAD")))
				.findAny()
				.orElse(null);
		assertNotNull(evidence);
		List<AnnotationProperty> property = annotationWithGnomadVariants.getProperties()
											.stream()
											.collect(Collectors.toList());
		assertEquals("GnomAD Allele Frequency", property.get(0).getName());

	}
}
