package com.nextprot.api.annotation.builder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.nextprot.api.commons.constants.AnnotationCategory;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.domain.annotation.IsoformAnnotation;
import org.nextprot.commons.statements.StatementBuilder;

public class AnnotationBuilderTest {
	
	@Test
	public void shouldReturnOneSingleAnnotationIfTheInfoIsTheSameAndItIsComingFromDifferentSources() {
		
		StatementBuilder sb1 = StatementBuilder.createNew().
		   addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component")
   		  .addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
   		  .addSourceInfo("CAVA-VP0920190912", "BioEditor");
		
		StatementBuilder sb2 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component")
				.addCvTerm("go-xxx", "nucleus", "go-cellular-component-cv")
				.addSourceInfo("HPA2222", "HPA");
		
		IsoformAnnotation annotation = AnnotationBuilder.buildAnnotation("NX_P01308-1", Arrays.asList(sb1.build(), sb2.build()));

		assertEquals(annotation.getAPICategory(), AnnotationCategory.GO_CELLULAR_COMPONENT);
		assertEquals(annotation.getEvidences().size(), 2);
		
	}


	@Test(expected = NextProtException.class)
	public void shouldReturnAnExceptionIf2AnnotationsAreExpectedInsteadOfOne() {
		
		StatementBuilder sb1 = StatementBuilder.createNew().
		   addCompulsaryFields("NX_P01308", "NX_P01308-1", "go-cellular-component");
   		
		StatementBuilder sb2 = StatementBuilder.createNew().
				addCompulsaryFields("NX_P99999", "NX_P99999-1", "go-cellular-component");
		
		AnnotationBuilder.buildAnnotation("NX_P01308-1", Arrays.asList(sb1.build(), sb2.build()));
		
	}

}
