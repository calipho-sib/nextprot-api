package org.nextprot.api.core.service.statement.service;

import java.util.Arrays;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;
import org.nextprot.commons.statements.StatementBuilder;

import org.junit.Assert;

public class StatementAnnotationBuilderTest {

	@Test
	public void testBuildAnnotationEvidenceKey() {

		AnnotationEvidence evidence = new AnnotationEvidence();
		evidence.setResourceId(691805);
		evidence.setExperimentalContextId(null);
		evidence.setNegativeEvidence(false);
		evidence.setAssignedBy("ENYO");
		evidence.setEvidenceCodeAC("ECO:0000353");

		AnnotationEvidenceProperty property = new AnnotationEvidenceProperty();
		property.setEvidenceId(1);
		property.setPropertyName("psimiId");
		property.setPropertyValue("MI:0943");

		evidence.setProperties(Arrays.asList(new AnnotationEvidenceProperty[] { property }));
		Assert.assertEquals("MI:0943", evidence.getPropertyValue("psimiId"));

		StatementBuilder statement = new StatementBuilder();

	}

}
