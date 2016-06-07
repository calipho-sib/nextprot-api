package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.ModifiedEntry;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextprot.api.annotation.builder.AnnotationBuilderBaseTest;

public class RawStatementServiceTest extends AnnotationBuilderBaseTest {

	@Autowired
	private RawStatementService rawStatementService;

	@Test
	public void findAllMappedStatements() {
		List<ModifiedEntry> modifiedEntry = rawStatementService.getModifiedEntryAnnotation("NX_P38398");
		System.out.println(modifiedEntry.size());
	}

}
