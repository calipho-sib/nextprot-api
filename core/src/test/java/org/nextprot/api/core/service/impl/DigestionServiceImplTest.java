package org.nextprot.api.core.service.impl;

import com.google.common.collect.Sets;
import org.expasy.mzjava.proteomics.mol.digest.Protease;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DigestionService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.annotation.comp.ByIsoformPositionComparatorTest;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.anyString;
import static org.nextprot.api.core.service.annotation.comp.ByIsoformPositionComparatorTest.mockAnnotation;

@ActiveProfiles({ "dev" })
public class DigestionServiceImplTest extends CoreUnitBaseTest {

	// See also this post on good practice for mocking http://tedvinke.wordpress.com/2014/02/13/mockito-why-you-should-not-use-injectmocks-annotation-to-autowire-fields/
	// Class under test
	private DigestionService digestionService;

	@Mock
	private AnnotationService annotationService;
	@Autowired
	private IsoformService isoformService;
	@Mock
	private MasterIdentifierService masterIdentifierService;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		digestionService = new DigestionServiceImpl(annotationService, isoformService, masterIdentifierService);
	}

    @Test
    public void shouldNotDigestWhenNoAnnotations() {

	    Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(new ArrayList<>());

	    Set<String> peptides = digestionService.digest("NX_P01308", Protease.TRYPSIN, 7, 77, 2);

	    Assert.assertTrue(peptides.isEmpty());
    }

	@Test
	public void shouldDigestMatureProteinAndPropeptides() {

		List<Annotation> annotations = new ArrayList<>();
		annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 25, 54)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 90, 110)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURATION_PEPTIDE, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 57, 87)));
		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(annotations);

		Set<String> peptides = digestionService.digest("NX_P01308", Protease.TRYPSIN, 7, 77, 2);

		Assert.assertTrue(peptides.stream().allMatch(peptide -> peptide.length() >= 7 && peptide.length() <= 77));
		Assert.assertEquals(7, peptides.size());
		// should find mature protein and propeptide
		Assert.assertTrue(peptides.containsAll(Arrays.asList("FVNQHLCGSHLVEALYLVCGERGFFYTPKT", "GIVEQCCTSICSLYQLENYCN", "EAEDLQVGQVELGGGPGAGSLQPLALEGSLQ")));
		// should find digests
		Assert.assertTrue(peptides.containsAll(Arrays.asList("FVNQHLCGSHLVEALYLVCGERGFFYTPK", "FVNQHLCGSHLVEALYLVCGER", "GFFYTPK", "GFFYTPKT")));
	}

	@Test
	public void shouldDigestAll() {

		List<Annotation> annotations = new ArrayList<>();
		annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 25, 54)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 90, 110)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURATION_PEPTIDE, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 57, 87)));
		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(annotations);
		Mockito.when(masterIdentifierService.findUniqueNames()).thenReturn(Sets.newHashSet("NX_P01308"));

		Set<String> peptides = digestionService.digestAllWithTrypsin();

		Assert.assertTrue(peptides.stream().allMatch(peptide -> peptide.length() >= 7 && peptide.length() <= 77));
		Assert.assertEquals(7, peptides.size());
		// should find mature protein and propeptide
		Assert.assertTrue(peptides.containsAll(Arrays.asList("FVNQHLCGSHLVEALYLVCGERGFFYTPKT", "GIVEQCCTSICSLYQLENYCN", "EAEDLQVGQVELGGGPGAGSLQPLALEGSLQ")));
		// should find digests
		Assert.assertTrue(peptides.containsAll(Arrays.asList("FVNQHLCGSHLVEALYLVCGERGFFYTPK", "FVNQHLCGSHLVEALYLVCGER", "GFFYTPK", "GFFYTPKT")));
	}
}