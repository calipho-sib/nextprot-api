package org.nextprot.api.core.domain;

import org.junit.Test;
import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AnnotationTest {

	@Test
	public void testOneHigh() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	@Test
	public void testTwoHigh() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("high"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	@Test
	public void testHighAndLow() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("low"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	@Test
	public void testHighAndPositive() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("positive"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	@Test
	public void testOneNegative() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("negative"));
		a.setEvidences(evidences);
        assertEquals("undetectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	@Test
	public void testTwoNegative() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("negative"));
		evidences.add(buildEvidence("negative"));
		a.setEvidences(evidences);
        assertEquals("undetectedExpression", a.getConsensusExpressionLevelPredicat());
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
		assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
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
        assertEquals(null, a.getConsensusExpressionLevelPredicat());
	}

	/*
	 * this case seems to occur (in dev at least)
	 */
	@Test
	public void testNoEvidence() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		a.setEvidences(evidences);
        assertEquals(null, a.getConsensusExpressionLevelPredicat());
	}

	@Test
	public void testOneNotDetected() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<>();
		evidences.add(buildEvidence("not detected"));
		a.setEvidences(evidences);
		assertEquals("undetectedExpression", a.getConsensusExpressionLevelPredicat());
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
		assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	private AnnotationEvidence buildEvidence(String level) {
		AnnotationEvidenceProperty p = new AnnotationEvidenceProperty();
		p.setPropertyName("expressionLevel");
		p.setPropertyValue(level);
		List<AnnotationEvidenceProperty> props = new ArrayList<AnnotationEvidenceProperty>();
		props.add(p);
		AnnotationEvidence ev = new AnnotationEvidence();
		ev.setProperties(props);
		return ev;
	}
}
