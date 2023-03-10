package org.nextprot.api.core.service.statement.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.ExperimentalContext;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.service.AnnotationBuilderIntegrationBaseTest;
import org.nextprot.api.core.service.ExperimentalContextService;
import org.nextprot.api.core.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatementServiceTest extends AnnotationBuilderIntegrationBaseTest {

	@Autowired
	private StatementService statementService;

	@Autowired
	private ExperimentalContextService contextService;

	/*
	@Test
	public void findAllMappedStatements() {
		List<IsoformAnnotation> modifiedIsoformAnnotation = statementService.getIsoformAnnotations("NX_Q9BX63-1");
		System.out.println(modifiedIsoformAnnotation.size());
	}*/

	//TODO: this test do nothing and f90790567dc0650b5737fa62332bb38a does not exist
	//TODO to unignore
	@Ignore
	@Test
	public void findAllNormalAnnotations() {

		List<Annotation> isoformAnnotations = statementService.getAnnotations("NX_P52701");
		List<Annotation> list = isoformAnnotations.stream().filter(a -> a.getAPICategory().equals(AnnotationCategory.PROTEIN_PROPERTY))
				.collect(Collectors.toList());
		Assert.assertTrue(list.get(0).getQualityQualifier().equals("GOLD"));

		//System.out.println(list.get(0));
	}

	/*
	@Test
	public void findModifiedAnnotationsByIsoform() {
		List isoformAnnotations = statementService.getIsoformAnnotations("NX_Q15858-3");
		System.out.println(isoformAnnotations.size());
	}

	@Test
	public void findPhenotypeAnnotationsGoldOnly() {
		List<IsoformAnnotation> isoformAnnotations = statementService.getIsoformAnnotations("NX_Q15858-3");
		Integer notFilterCount = AnnotationUtils.filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(isoformAnnotations, null).size();
		Integer notFilterCount2 = AnnotationUtils.filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(isoformAnnotations, false).size();
		Integer filterCount = AnnotationUtils.filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(isoformAnnotations, true).size();
		
		assertEquals(notFilterCount, notFilterCount2);
		assertTrue(filterCount < notFilterCount2);
		
	}
	*/

	// TODO: USELESS TEST
    @Test
    public void findSimpleGlycoAnnotations() {

        List<Annotation> annotations = statementService.getAnnotations("NX_O75503");
    }
	
	@Test
	public void findBgeeAnnotations() {
		List<Annotation> annotations = statementService.getAnnotations("NX_O43657");

		Set<Annotation> annotContextNull = annotations.stream()
													  .filter(ba -> ba.getAPICategory().equals(AnnotationCategory.EXPRESSION_PROFILE))
													  .filter(ba -> ba.getEvidences().stream().anyMatch(e -> e.getExperimentalContextId() == null))
													  .collect(Collectors.toSet());
		Assert.assertTrue("Should not find annotations with no exp. context: " + annotContextNull, annotContextNull.isEmpty());

		List<Annotation> subset = annotations.stream()
											 .filter(ba -> ba.getAPICategory().equals(AnnotationCategory.EXPRESSION_PROFILE))
											 .filter(a -> a.getCvTermAccessionCode() != null
													 && a.getCvTermAccessionCode().equals("TS-0741"))
											 .collect(Collectors.toList());
		Assert.assertEquals(1, subset.size());


		Assert.assertEquals(1, subset.size());
		Annotation a = subset.get(0);

		Assert.assertEquals("tissue specificity", a.getCategory());
		Assert.assertEquals("SILVER", a.getQualityQualifier());
		Assert.assertTrue(a.getEvidences().size() >= 5);
		// Currently, in statements (nxflat) we have only expression data from Bgee only
		Assert.assertTrue(a.getEvidences().stream().allMatch(e -> e.getResourceDb().equals("Bgee")));
		Assert.assertTrue(a.getEvidences().stream().allMatch(e -> e.getResourceType().equals("database")));
		Assert.assertTrue(a.getEvidences().stream().allMatch(e -> e.getResourceAccession().equals("ENSG00000000003")));
		if (todayIsAfter("17 Nov 2020")) {
			// it should be lower casae in db "curated", not "CURATED"
			Assert.assertTrue(a.getEvidences().stream().allMatch(e -> e.getAssignmentMethod().equalsIgnoreCase("curated")));
		}
		Assert.assertTrue(a.getEvidences().stream()
						   .allMatch(e -> e.getPropertiesNames().containsAll(Arrays.asList("expressionLevel", "expressionScore"))));
		List<String> allowedEcoForBgee = Arrays.asList("ECO:0000104", "ECO:0000295", "ECO:0000009");
		Assert.assertTrue(a.getEvidences().stream().allMatch(e -> allowedEcoForBgee.contains(e.getEvidenceCodeAC())));

		Set<Long> contextIds = subset.stream()
									 .map(ba -> ba.getEvidences()
												  .stream()
												  .map(AnnotationEvidence::getExperimentalContextId)
												  .collect(Collectors.toSet()))
									 .flatMap(Set::stream)
									 .collect(Collectors.toSet());
		Assert.assertTrue(contextIds.size() >= 5);
		List<ExperimentalContext> experimentalContexts = contextService.findExperimentalContextsByIds(contextIds);
		Assert.assertTrue(experimentalContexts.stream().allMatch(c -> c.getTissueAC() != null));
		Assert.assertTrue(experimentalContexts.stream().allMatch(c -> c.getTissueAC().equals("TS-0741")));
		Assert.assertTrue(experimentalContexts.stream().allMatch(c -> c.getDevelopmentalStage() != null));
		Assert.assertTrue(experimentalContexts.stream().allMatch(c -> StringUtils.isNotBlank(c.getDevelopmentalStageAC())));
		Assert.assertTrue(experimentalContexts.stream().allMatch(c -> c.getDevelopmentalStage().getOntology().equals("BgeeDevelopmentalStageCv")));
		// Should have less experimental contexts than evidences
		// because some statements have same tissue/stage but different ECO method
		Assert.assertEquals("Should have less experimental contexts than evidences", contextIds.size(), a.getEvidences().size());
	}

	@Test
	public void findBgeeAnnotations_similarStatements() {
		List<Annotation> annotations = statementService.getAnnotations("NX_Q9NR63");
		// We have 2 statements with same stage and same CALOHA term but with different expressionLevel (and different score)

		List<Annotation> subset = annotations.stream()
											 .filter(ba -> ba.getAPICategory().equals(AnnotationCategory.EXPRESSION_PROFILE))
											 .filter(a -> a.getCvTermAccessionCode() != null
													 && a.getCvTermAccessionCode().equals("TS-2723"))
											 .collect(Collectors.toList());
		Assert.assertEquals(1, subset.size());

		Set<String> expressionLevels = subset.stream()
											.map(ba -> ba.getEvidences()
														 .stream()
														 .map(e -> e.getPropertyValue("expressionLevel"))
														 .collect(Collectors.toSet()))
											.flatMap(Set::stream)
											.collect(Collectors.toSet());
		Assert.assertEquals(new HashSet<>(Arrays.asList("detected", "not detected")), expressionLevels);

		Set<AnnotationEvidence> ev = subset.stream()
											 .map(ba -> ba.getEvidences())
											 .flatMap(List::stream)
											 .collect(Collectors.toSet());
		Set<Long> contextIds = ev.stream().map(e -> e.getExperimentalContextId()).collect(Collectors.toSet());
		// Should have less experimental contexts than evidences
		// because some statements have same tissue/stage but expressionLevel
		Assert.assertTrue("Should have less experimental contexts than evidences", contextIds.size() < ev.size());
	}

	public static boolean todayIsAfter(String date) {
		Date somedate = new Date(date);
		Date now = new Date();
		return now.after(somedate);

	}
}
