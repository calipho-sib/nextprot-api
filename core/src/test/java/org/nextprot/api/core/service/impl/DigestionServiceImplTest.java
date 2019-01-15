package org.nextprot.api.core.service.impl;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.nextprot.api.commons.bio.variation.prot.digestion.ProteinDigesterBuilder;
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

	    Set<String> peptides = digestionService.digestProteins("NX_P01308", new ProteinDigesterBuilder());

	    Assert.assertTrue(peptides.isEmpty());
    }

	@Test
	public void shouldDigestMatureProteinAndPropeptides() {

		List<Annotation> annotations = new ArrayList<>();
		annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 25, 54)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 90, 110)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURATION_PEPTIDE, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 57, 87)));
		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(annotations);

		Set<String> peptides = digestionService.digestProteins("NX_P01308",
				new ProteinDigesterBuilder().minPepLen(1).maxMissedCleavageCount(0));

		Assert.assertTrue(peptides.stream().allMatch(peptide -> peptide.length() > 0 && peptide.length() <= 77));
		Assert.assertEquals(5, peptides.size());
		Assert.assertTrue(peptides.containsAll(Arrays.asList("T", "EAEDLQVGQVELGGGPGAGSLQPLALEGSLQ", "GIVEQCCTSICSLYQLENYCN",
				"FVNQHLCGSHLVEALYLVCGER", "GFFYTPK")));
	}

	@Test
	public void shouldDigestRawProteinSequences() {

		Set<String> peptides = digestionService.digestProteins("NX_P01308",
				new ProteinDigesterBuilder().withMaturePartsOnly(false).minPepLen(1).maxMissedCleavageCount(0));

		Assert.assertTrue(peptides.stream().allMatch(peptide -> peptide.length() > 0 && peptide.length() <= 77));
		Assert.assertEquals(7, peptides.size());
		Assert.assertTrue(peptides.containsAll(Arrays.asList("MALWMR", "LLPLLALLALWGPDPAAAFVNQHLCGSHLVEALYLVCGER",
				"GFFYTPK", "TR", "R", "EAEDLQVGQVELGGGPGAGSLQPLALEGSLQK", "GIVEQCCTSICSLYQLENYCN")));
	}

	@Test
	public void shouldDigestAll() {

		List<Annotation> annotations = new ArrayList<>();
		annotations.add(mockAnnotation(1, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 25, 54)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURE_PROTEIN, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 90, 110)));
		annotations.add(mockAnnotation(2, AnnotationCategory.MATURATION_PEPTIDE, new ByIsoformPositionComparatorTest.TargetIsoform("NX_P01308-1", 57, 87)));
		Mockito.when(annotationService.findAnnotations(anyString())).thenReturn(annotations);
		Mockito.when(masterIdentifierService.findUniqueNames()).thenReturn(Sets.newHashSet("NX_P01308"));

		Set<String> peptides = digestionService.digestAllMatureProteinsWithTrypsin();

		Assert.assertTrue(peptides.stream().allMatch(peptide -> peptide.length() >= 7 && peptide.length() <= 77));
		Assert.assertEquals(7, peptides.size());
		// should find mature protein and propeptide
		Assert.assertTrue(peptides.containsAll(Arrays.asList("FVNQHLCGSHLVEALYLVCGERGFFYTPKT", "GIVEQCCTSICSLYQLENYCN", "EAEDLQVGQVELGGGPGAGSLQPLALEGSLQ")));
		// should find digests
		Assert.assertTrue(peptides.containsAll(Arrays.asList("FVNQHLCGSHLVEALYLVCGERGFFYTPK", "FVNQHLCGSHLVEALYLVCGER", "GFFYTPK", "GFFYTPKT")));
	}

	@Test
	public void shouldListProteaseNames() {

		List<String> proteaseNames = digestionService.getProteaseNames();

		Assert.assertEquals(Arrays.asList("ARG_C", "ASP_N", "BNPS_SKATOLE", "CASPASE_1", "CASPASE_10", "CASPASE_2",
				"CASPASE_3", "CASPASE_4", "CASPASE_5", "CASPASE_6", "CASPASE_7", "CASPASE_8", "CASPASE_9",
				"CHYMOTRYPSIN_FYL", "CHYMOTRYPSIN_FYLW", "CHYMOTRYPSIN_HIGH_SPEC", "CHYMOTRYPSIN_LOW_SPEC", "CNBR",
				"ENTEROKINASE", "GLU_C_BICARBONATE", "GLU_C_PHOSPHATE", "LYS_C", "PEPSIN_PH_1_3", "PEPSIN_PH_GT_2",
				"PROTEINASE_K", "THERMOLYSIN", "TRYPSIN"), proteaseNames);
	}
}