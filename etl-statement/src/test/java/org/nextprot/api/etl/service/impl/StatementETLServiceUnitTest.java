package org.nextprot.api.etl.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import com.nextprot.api.annotation.builder.statement.TargetIsoformSerializer;

public class StatementETLServiceUnitTest extends StatementETLBaseUnitTest {

	@Test
	public void shouldComputeCorrectTargetIsoformsForVariants() throws Exception {
		
		Set<Statement> subjectStatements = new HashSet<Statement>(
				Arrays.asList(StatementBuilder.createNew()
						.addField(StatementField.ENTRY_ACCESSION, "NX_Q15858")
						.addField(StatementField.ANNOTATION_NAME, "SCN9A-iso3-p.Ile848Thr")
				.build()));
		
		List<Statement> variantOnEntry = 
				StatementTransformationUtil.getPropagatedStatementsForEntry(
						isoformMappingServiceMocked, subjectStatements, "NX_Q15858");


		//It should return only one statement with target isoforms
		Assert.assertTrue(variantOnEntry.size() == 1);
		
		String targetIsoformsString = variantOnEntry.iterator().next().getValue(StatementField.TARGET_ISOFORMS);

		Set<TargetIsoformStatementPosition> targetIsoforms = TargetIsoformSerializer.deSerializeFromJsonString(targetIsoformsString);
		
		Assert.assertEquals(targetIsoforms.size(), 4);
		TargetIsoformStatementPosition pos1 = targetIsoforms.stream().filter(ti -> ti.getIsoformAccession().equals("NX_Q15858-1")).collect(Collectors.toList()).get(0);
		Assert.assertTrue(pos1.getBegin().equals(859));
		Assert.assertTrue(pos1.getName().equals("SCN9A-iso1-p.Ile859Thr"));

	}
	
	@Test
	public void shouldComputeCorrectTargetIsoformsForProteoformAnnotations() throws Exception {

		
		Set<Statement> rawSubjectStatements = new HashSet<Statement>(
				Arrays.asList(StatementBuilder.createNew()
						.addField(StatementField.ENTRY_ACCESSION, "NX_Q15858")
						.addField(StatementField.ANNOTATION_NAME, "SCN9A-iso3-p.Met932Leu")
				.build(),
				StatementBuilder.createNew()
						.addField(StatementField.ENTRY_ACCESSION, "NX_Q15858")
						.addField(StatementField.ANNOTATION_NAME, "SCN9A-iso3-p.Val991Leu")
				.build()));
		
		
		List<Statement> subjectStatements = 
				StatementTransformationUtil.getPropagatedStatementsForEntry(
						isoformMappingServiceMocked, rawSubjectStatements, "NX_Q15858");

		Statement proteoformStatement = StatementBuilder.createNew()
				.addField(StatementField.ENTRY_ACCESSION, "NX_Q15858")
				.build();
		
		{

		    Set<TargetIsoformStatementPosition> result = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(proteoformStatement, isoformMappingServiceMocked, subjectStatements, true, "NX_Q15858-3", Arrays.asList("NX_Q15858-1", "NX_Q15858-2", "NX_Q15858-3", "NX_Q15858-4"));
			
		    Assert.assertTrue(result.size() == 1);
		    Assert.assertTrue(result.iterator().next().getName().equals("SCN9A-iso3-p.Met932Leu + SCN9A-iso3-p.Val991Leu"));
		    Assert.assertTrue(result.iterator().next().getSpecificity().equals(IsoTargetSpecificity.SPECIFIC.name()));

		}
	

		{

			Set<TargetIsoformStatementPosition> result = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(proteoformStatement, isoformMappingServiceMocked, subjectStatements, false, null, Arrays.asList("NX_Q15858-1", "NX_Q15858-2", "NX_Q15858-3", "NX_Q15858-4"));
		
		    System.err.println(result.size());
		    Assert.assertEquals(result.size(), 2); //Because SCN9A-iso1-p.Val991Leu can only be propagated on 1 and 3
		    Assert.assertTrue(result.iterator().next().getName().equals("SCN9A-iso1-p.Met943Leu + SCN9A-iso1-p.Val1002Leu"));
		    Assert.assertTrue(result.iterator().next().getSpecificity().equals(IsoTargetSpecificity.BY_DEFAULT.name()));
		}
		
	}
	

	
}