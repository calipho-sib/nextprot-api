package org.nextprot.api.etl.service.impl;

import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.etl.statement.StatementETLBaseUnitTest;
import org.nextprot.commons.statements.Statement;


public class StatementTransformBDDTest extends StatementETLBaseUnitTest {

	
	@Test
	public void shouldThrowAnExceptionWhenMultipleMutantsAreLocatedOnDifferentGenes() {
	
		try {
			StatementsExtractorLocalMockImpl sle = new StatementsExtractorLocalMockImpl();
			Set<Statement> rawStatements = sle.getStatementsForSourceForGeneName(null, "msh2-msh6-multiple-mutants-on-different-genes");

			statementETLServiceMocked.transformStatements(rawStatements);
			
			fail();
			
		}catch(NextProtException e){
			
			Assert.assertEquals("Mixing iso numbers for subjects is not allowed", e.getMessage());
			Assert.assertEquals(NextProtException.class, e.getClass());
			
		}
	
	}
	
	
	@Test
	public void shouldThrowAnExceptionIfFeatureNameDoesNotCorrespondToNextprotAccession() {
		
		
	}

}
