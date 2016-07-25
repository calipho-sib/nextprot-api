package com.nextprot.api.annotation.builder.statement.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.api.core.utils.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class RawStatementServiceTest extends AnnotationBuilderBaseTest {

	@Autowired
	private StatementService rawStatementService;
	

	@Test
	public void findAllMappedStatements() {
		List<IsoformAnnotation> modifiedIsoformAnnotation = rawStatementService.getProteoformIsoformAnnotations("NX_Q9BX63");
		System.out.println(modifiedIsoformAnnotation.size());
	}

	@Test
	public void findAllNormalAnnotations() {
		List isoformAnnotations = rawStatementService.getNormalIsoformAnnotations("NX_Q9BX63");
		System.out.println(isoformAnnotations.size());
	}
	
	
	@Test
	public void findModifiedAnnotationsByIsoform() {
		List isoformAnnotations = rawStatementService.getProteoformIsoformAnnotations("NX_Q15858-3");
		System.out.println(isoformAnnotations.size());
	}
	
	
	
	
	@Test
	public void findPhenotypeAnnotationsGoldOnly() {
		List<IsoformAnnotation> isoformAnnotations = rawStatementService.getProteoformIsoformAnnotations("NX_Q9UHC1");
		Integer notFilterCount = AnnotationUtils.filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(isoformAnnotations, null).size();
		Integer notFilterCount2 = AnnotationUtils.filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(isoformAnnotations, false).size();
		Integer filterCount = AnnotationUtils.filterAnnotationsByGoldOnlyCarefulThisChangesAnnotations(isoformAnnotations, true).size();
		
		assertEquals(notFilterCount, notFilterCount2);
		assertTrue(filterCount < notFilterCount2);
		
	}
	
	
}
