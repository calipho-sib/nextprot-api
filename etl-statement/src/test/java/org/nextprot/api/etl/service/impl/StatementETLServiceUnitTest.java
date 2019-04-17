package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.specs.CoreStatementField.*;

public class StatementETLServiceUnitTest extends StatementETLBaseUnitTest {

	@Test
	public void shouldComputeCorrectTargetIsoformsForVariants() {
		
		List<Statement> subjectStatements =
				Collections.singletonList(new StatementBuilder()
						.addField(ENTRY_ACCESSION, "NX_Q15858")
						.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Ile848Thr")
						.addField(ANNOTATION_CATEGORY, "variant")
						.build());
		
		List<Statement> variantOnEntry =
				simpleStatementTransformerService.transformVariantAndMutagenesisSet(subjectStatements);


		//It should return only one statement with target isoforms
		Assert.assertTrue(variantOnEntry.size() == 1);
		
		String targetIsoformsString = variantOnEntry.iterator().next().getValue(TARGET_ISOFORMS);

		Set<TargetIsoformStatementPosition> targetIsoforms = TargetIsoformSet.deSerializeFromJsonString(targetIsoformsString);
		
		Assert.assertEquals(targetIsoforms.size(), 4);
		TargetIsoformStatementPosition pos1 = targetIsoforms.stream().filter(ti -> ti.getIsoformAccession().equals("NX_Q15858-1")).collect(Collectors.toList()).get(0);
		Assert.assertTrue(pos1.getBegin().equals(859));
		Assert.assertTrue(pos1.getName().equals("SCN9A-iso1-p.Ile859Thr"));

	}
	
	@Test
	public void shouldComputeCorrectTargetIsoformsForProteoformAnnotations() {

		List<Statement> rawSubjectStatements = new ArrayList<>(
				Arrays.asList(
						new StatementBuilder()
						.addField(ENTRY_ACCESSION, "NX_Q15858")
						.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Met932Leu")
						.addField(ANNOTATION_CATEGORY, "variant")
						.build(),
						new StatementBuilder()
						.addField(ENTRY_ACCESSION, "NX_Q15858")
						.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Val991Leu")
						.addField(ANNOTATION_CATEGORY, "variant")
						.build()));

		List<Statement> subjectStatements =
				simpleStatementTransformerService.transformVariantAndMutagenesisSet(rawSubjectStatements);

		Set<TargetIsoformStatementPosition> result = statementIsoformPositionService.computeTargetIsoformsForProteoformAnnotation(
				subjectStatements, true, "NX_Q15858-3");

	    Assert.assertTrue(result.size() == 1);
	    Assert.assertTrue(result.iterator().next().getName().equals("SCN9A-iso3-p.Met932Leu + SCN9A-iso3-p.Val991Leu"));
	    Assert.assertTrue(result.iterator().next().getSpecificity().equals(IsoTargetSpecificity.SPECIFIC.name()));


		result = statementIsoformPositionService.computeTargetIsoformsForProteoformAnnotation(subjectStatements, false, null);

	    System.err.println(result.size());
	    Assert.assertEquals(result.size(), 2); //Because SCN9A-iso1-p.Val991Leu can only be propagated on 1 and 3
	    Assert.assertTrue(result.iterator().next().getName().equals("SCN9A-iso1-p.Met943Leu + SCN9A-iso1-p.Val1002Leu"));
	    Assert.assertTrue(result.iterator().next().getSpecificity().equals(IsoTargetSpecificity.UNKNOWN.name()));
	}
}
