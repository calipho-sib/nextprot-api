package com.nextprot.api.annotation.builder.statement.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.utils.annot.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class StatementServiceTest extends AnnotationBuilderBaseTest {

	@Autowired
	private StatementService statementService;
	

	@Test
	public void findAllMappedStatements() {
		List<IsoformAnnotation> modifiedIsoformAnnotation = statementService.getIsoformAnnotations("NX_Q9BX63-1");
		System.out.println(modifiedIsoformAnnotation.size());
	}

	@Test
	public void findAllNormalAnnotations() {
		List<Annotation> isoformAnnotations = statementService.getAnnotations("NX_Q15858");
		Annotation a = isoformAnnotations.stream().filter(ia -> ia.getAnnotationHash().equals("f90790567dc0650b5737fa62332bb38a")).collect(Collectors.toList()).iterator().next();
		
		System.out.println(a.getSpecificityForIsoform("NX_Q15858-1"));
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
