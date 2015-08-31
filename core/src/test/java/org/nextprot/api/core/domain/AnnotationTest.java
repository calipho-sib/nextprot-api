package org.nextprot.api.core.domain;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;

import java.util.ArrayList;
import java.util.List;

public class AnnotationTest {

	@Test
	public void testOneHigh() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	@Test
	public void testTwoHigh() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("high"));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	@Test
	public void testHighAndLow() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("low"));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	@Test
	public void testHighAndPositive() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("positive"));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	@Test
	public void testOneNegative() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("negative"));
		a.setEvidences(evidences);
		Assert.assertTrue(!a.isExpressionLevelDetected());
	}

	@Test
	public void testTwoNegative() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("negative"));
		evidences.add(buildEvidence("negative"));
		a.setEvidences(evidences);
		Assert.assertTrue(!a.isExpressionLevelDetected());
	}

	/*
	 * at least one detected, low, high or medium => detected !
     */
	@Test
	public void testNegativeAndPositive() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("negative"));
		evidences.add(buildEvidence("positive"));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	/*
	 * occurs for every annotation not being a :ExpressionProfile
	 */
	@Test
	public void testNoExpressionLevel() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(new AnnotationEvidence());
		a.setEvidences(evidences);
		Assert.assertTrue(!a.isExpressionLevelDetected());
	}

	/*
	 * this case seems to occur (in dev at least)
	 */
	@Test
	public void testNoEvidence() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		a.setEvidences(evidences);
		Assert.assertTrue(!a.isExpressionLevelDetected());
	}

	// not sure about that, should return false imo
	@Test
	public void testEmptyExpressionLevel() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence(""));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	@Test
	public void testOneNotDetected() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("not detected"));
		a.setEvidences(evidences);
		Assert.assertTrue(!a.isExpressionLevelDetected());
	}

	/*
	 * at leat one detected, low, high or medium => detected !
	 */
	@Test
	public void testNotDetectedAndPositive() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("not detected"));
		evidences.add(buildEvidence("positive"));
		a.setEvidences(evidences);
		Assert.assertTrue(a.isExpressionLevelDetected());
	}

	private AnnotationEvidence buildEvidence(String level) {
		AnnotationEvidenceProperty p = new AnnotationEvidenceProperty();
		p.setPropertyName("expressionLevel");
		p.setPropertyValue(level);
		List<AnnotationEvidenceProperty> props = new ArrayList<>();
		props.add(p);
		AnnotationEvidence ev = new AnnotationEvidence();
		ev.setProperties(props);
		return ev;
	}
}
