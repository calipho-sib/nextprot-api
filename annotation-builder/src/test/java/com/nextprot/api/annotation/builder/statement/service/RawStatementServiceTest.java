package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class RawStatementServiceTest extends AnnotationBuilderBaseTest {

	@Autowired
	private RawStatementService rawStatementService;
	

	@Test
	public void findAllMappedStatements() {
		List<IsoformAnnotation> modifiedIsoformAnnotation = rawStatementService.getModifiedIsoformAnnotationsByIsoform("NX_Q9BX63");
		System.out.println(modifiedIsoformAnnotation.size());
	}

	@Test
	public void findAllNormalAnnotations() {
		List isoformAnnotations = rawStatementService.getNormalAnnotations("NX_Q9BX63");
		System.out.println(isoformAnnotations.size());
	}
	
	
	
}
