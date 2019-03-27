package org.nextprot.api.etl.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.constants.IsoTargetSpecificity;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.TargetIsoformSet;
import org.nextprot.commons.statements.TargetIsoformStatementPosition;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nextprot.commons.statements.NXFlatTableStatementField.*;

public class StatementETLServiceUnitTest extends StatementETLBaseUnitTest {

	@Test
	public void shouldComputeCorrectTargetIsoformsForVariants() throws Exception {
		
		Set<Statement> subjectStatements = new HashSet<>(
				Arrays.asList(StatementBuilder.createNew()
						.addField(ENTRY_ACCESSION, "NX_Q15858")
						.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Ile848Thr")
				.generateHashAndBuild()));
		
		List<Statement> variantOnEntry = 
				StatementTransformationUtil.getPropagatedStatementVariantsForEntry(
						isoformMappingServiceMocked, subjectStatements, "NX_Q15858");


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
	public void shouldComputeCorrectTargetIsoformsForProteoformAnnotations() throws Exception {

		
		Set<Statement> rawSubjectStatements = new HashSet<>(
				Arrays.asList(StatementBuilder.createNew()
						.addField(ENTRY_ACCESSION, "NX_Q15858")
						.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Met932Leu")
				.generateHashAndBuild(),
				StatementBuilder.createNew()
						.addField(ENTRY_ACCESSION, "NX_Q15858")
						.addField(ANNOTATION_NAME, "SCN9A-iso3-p.Val991Leu")
				.generateHashAndBuild()));
		
		
		List<Statement> subjectStatements = 
				StatementTransformationUtil.getPropagatedStatementVariantsForEntry(
						isoformMappingServiceMocked, rawSubjectStatements, "NX_Q15858");

		Statement proteoformStatement = StatementBuilder.createNew()
				.addField(ENTRY_ACCESSION, "NX_Q15858")
				.generateHashAndBuild();
		
		{

		    Set<TargetIsoformStatementPosition> result = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(subjectStatements, true, "NX_Q15858-3", Arrays.asList("NX_Q15858-1", "NX_Q15858-2", "NX_Q15858-3", "NX_Q15858-4"));
			
		    Assert.assertTrue(result.size() == 1);
		    Assert.assertTrue(result.iterator().next().getName().equals("SCN9A-iso3-p.Met932Leu + SCN9A-iso3-p.Val991Leu"));
		    Assert.assertTrue(result.iterator().next().getSpecificity().equals(IsoTargetSpecificity.SPECIFIC.name()));

		}
	

		{

			Set<TargetIsoformStatementPosition> result = StatementTransformationUtil.computeTargetIsoformsForProteoformAnnotation(subjectStatements, false, null, Arrays.asList("NX_Q15858-1", "NX_Q15858-2", "NX_Q15858-3", "NX_Q15858-4"));
		
		    System.err.println(result.size());
		    Assert.assertEquals(result.size(), 2); //Because SCN9A-iso1-p.Val991Leu can only be propagated on 1 and 3
		    Assert.assertTrue(result.iterator().next().getName().equals("SCN9A-iso1-p.Met943Leu + SCN9A-iso1-p.Val1002Leu"));
		    Assert.assertTrue(result.iterator().next().getSpecificity().equals(IsoTargetSpecificity.UNKNOWN.name()));
		}
		
	}


	
}
