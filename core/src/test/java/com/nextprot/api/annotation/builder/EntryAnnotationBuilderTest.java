package com.nextprot.api.annotation.builder;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.commons.statements.Statement;
import org.nextprot.commons.statements.StatementBuilder;
import org.nextprot.commons.statements.StatementField;

public class EntryAnnotationBuilderTest extends AnnotationBuilderBastUnitTest{
	
	@Test
	public void shouldFindCorrectPublicationId() {

		Statement sb1 = StatementBuilder.createNew().addField(StatementField.REFERENCE_ACCESSION, "123").build();
		
		AnnotationBuilder ab = EntryAnnotationBuilder.newBuilder(terminologyService, publicationService);
		AnnotationEvidence evidence = new AnnotationEvidence();
		ab.setEvidenceResourceId(evidence, sb1);
		Assert.assertEquals(evidence.getResourceId(), 999);
		
	}

	
	
	@Test(expected = NextProtException.class)
	public void shouldThrowAnExceptionIfInModeStrictAndPublicationIsNotFound() {

		Statement sb1 = StatementBuilder.createNew().addField(StatementField.REFERENCE_ACCESSION, "000").build();
		AnnotationBuilder ab = EntryAnnotationBuilder.newBuilder(terminologyService, publicationService);
		AnnotationEvidence evidence = new AnnotationEvidence();
		ab.setEvidenceResourceId(evidence, sb1);
		
	}
	
	@Override
	protected EntryAnnotationBuilder newAnnotationBuilder() {
		return EntryAnnotationBuilder.newBuilder(terminologyService, publicationService);
	}


}
