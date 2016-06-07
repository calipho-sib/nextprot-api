package com.nextprot.api.annotation.builder.statement.service;

import java.util.List;

import org.junit.Test;
import org.nextprot.api.core.domain.ModifiedEntry;
import org.nextprot.api.core.test.base.CoreUnitBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import jdk.nashorn.internal.ir.annotations.Ignore;

@ActiveProfiles({ "dev", "cache" })
@Ignore
public class RawStatementServiceTest extends CoreUnitBaseTest {

	@Autowired
	private RawStatementService rawStatementService;

	@Test
	public void findAllMappedStatements() {
		List<ModifiedEntry> modifiedEntry = rawStatementService.getModifiedEntryAnnotation("NX_P38398");
		System.out.println(modifiedEntry.size());
	}

}
