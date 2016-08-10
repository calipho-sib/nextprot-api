package com.nextprot.api.annotation.builder.statement.service;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatementServiceTest extends AnnotationBuilderBaseTest {

	@Autowired
	private StatementService statementService;
	
	@Test
	public void findAllMappedStatements() {
		List<IsoformAnnotation> modifiedIsoformAnnotation = statementService.getIsoformAnnotations("NX_Q9BX63-1");
		System.out.println(modifiedIsoformAnnotation.size());
	}

	// TODO: this test do nothing and f90790567dc0650b5737fa62332bb38a does not exist
	@Ignore
	@Test
	public void findAllNormalAnnotations() {
		List<Annotation> isoformAnnotations = statementService.getAnnotations("NX_Q15858");
		List<Annotation> list = isoformAnnotations.stream().filter(ia -> ia.getAnnotationHash().equals("f90790567dc0650b5737fa62332bb38a")).collect(Collectors.toList());

		Assert.assertTrue(!list.isEmpty());

		System.out.println(list.get(0).getSpecificityForIsoform("NX_Q15858-1"));
	}

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
	
	
}
