package org.nextprot.api.domain;

import junit.framework.TestCase;

import org.nextprot.api.domain.annotation.AnnotationVariant;

public class AnnotationVariantTest extends TestCase {

	public void testParse1() {
		String rd = null;
		AnnotationVariant av = new AnnotationVariant("I","L", rd);
		assertEquals(av.getDescription(), null);
		assertEquals(av.getDiseaseTerms(), null);
	}

	public void testParse2() {
		String rd = "this is just a comment";
		AnnotationVariant av = new AnnotationVariant("I","L", rd);
		assertEquals(av.getDescription(), "this is just a comment");
		assertEquals(av.getDiseaseTerms(), null);
	}
	
	public void testParse3() {
		String rd = "In [GLC3A:UNIPROT_DISEASE:DI-00935].";
		AnnotationVariant av = new AnnotationVariant("I","L", rd);
		assertEquals(av.getDescription(), null);
		assertEquals(av.getDiseaseTerms().size(), 1);
		assertEquals(av.getDiseaseTerms().get(0), "DI-00935");
	}
	
	public void testParse4() {
		String rd = "In [GLC3A:UNIPROT_DISEASE:DI-00935]; acts as GLC1A.";
		AnnotationVariant av = new AnnotationVariant("I","L", rd);
		assertEquals(av.getDescription(), "acts as GLC1A.");
		assertEquals(av.getDiseaseTerms().size(), 1);
		assertEquals(av.getDiseaseTerms().get(0), "DI-00935");
	}
	
	public void testParse5() {
		String rd = 
			"In [GLC3A:UNIPROT_DISEASE:DI-00935] and " + 
		    "[GLC1A:UNIPROT_DISEASE:DI-00937].";
		AnnotationVariant av = new AnnotationVariant("I","L", rd);
		assertEquals(av.getDescription(), null);
		assertEquals(av.getDiseaseTerms().size(), 2);
		assertEquals(av.getDiseaseTerms().get(0), "DI-00935");
		assertEquals(av.getDiseaseTerms().get(1), "DI-00937");
	}
	
	public void testParse6() {
		String rd = 
			"In [GLC3A:UNIPROT_DISEASE:DI-00935] and " + 
		    "[GLC1A:UNIPROT_DISEASE:DI-00937]; acts as GLC1A " + 
			"disease modifier in patients also carrying Val-399 mutation in MYOC";
		AnnotationVariant av = new AnnotationVariant("I","L", rd);
		assertEquals(av.getDescription(), "acts as GLC1A disease modifier in patients also carrying Val-399 mutation in MYOC");
		assertEquals(av.getDiseaseTerms().size(), 2);
		assertEquals(av.getDiseaseTerms().get(0), "DI-00935");
		assertEquals(av.getDiseaseTerms().get(1), "DI-00937");
	}
	
	
}
