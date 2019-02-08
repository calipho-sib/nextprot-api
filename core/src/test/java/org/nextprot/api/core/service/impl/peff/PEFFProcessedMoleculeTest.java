package org.nextprot.api.core.service.impl.peff;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class PEFFProcessedMoleculeTest extends CoreUnitBaseTest {

	@Mock
	private AnnotationService annotationService;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);

		Annotation a1 = mockAnnotation(AnnotationCategory.SIGNAL_PEPTIDE, 1, 40);
		Annotation a2 = mockAnnotation(AnnotationCategory.MATURE_PROTEIN, 41, 890);

		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(Arrays.asList(a1, a2));
	}

	@Test
	public void formatProcessedMolecule() {

		List<Annotation> isoformAnnotations = annotationService.findAnnotations("NX_Q06418");

		PEFFInformation variantSimple = new PEFFProcessedMolecule("NX_P43246-1", isoformAnnotations);

		Assert.assertEquals("\\Processed=(1|40|PEFF:0001021|signal peptide)(41|890|PEFF:0001020|mature protein)",
				variantSimple.format());
	}

	private static Annotation mockAnnotation(AnnotationCategory cat, int from, int to) {

		Annotation mock = Mockito.mock(Annotation.class);

		when(mock.getAPICategory()).thenReturn(cat);
		when(mock.getStartPositionForIsoform(anyString())).thenReturn(from);
		when(mock.getEndPositionForIsoform(anyString())).thenReturn(to);

		return mock;
	}
}