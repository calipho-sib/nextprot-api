package org.nextprot.api.core.service.statement.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.service.AnnotationBuilderIntegrationBaseTest;
import org.nextprot.api.core.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class StatementServiceTest extends AnnotationBuilderIntegrationBaseTest {

	@Autowired
	private StatementService statementService;
	/*
	@Test
	public void findAllMappedStatements() {
		List<IsoformAnnotation> modifiedIsoformAnnotation = statementService.getIsoformAnnotations("NX_Q9BX63-1");
		System.out.println(modifiedIsoformAnnotation.size());
	}*/

	// TODO: this test do nothing and f90790567dc0650b5737fa62332bb38a does not exist
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
	
}
