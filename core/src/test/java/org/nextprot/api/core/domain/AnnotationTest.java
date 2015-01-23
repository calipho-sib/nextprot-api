package org.nextprot.api.core.domain;

import java.util.ArrayList;
import java.util.List;

import org.nextprot.api.core.domain.annotation.Annotation;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;
import org.nextprot.api.core.domain.annotation.AnnotationEvidenceProperty;

import junit.framework.TestCase;

public class AnnotationTest extends TestCase {

	public void testOneHigh() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("high"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}
	
	public void testTwoHigh() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("high"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	public void testHighAndLow() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("low"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	public void testHighAndPositive() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("high"));
		evidences.add(buildEvidence("positive"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	public void testOneNegative() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("negative"));
		a.setEvidences(evidences);
        assertEquals("undetectedExpression", a.getConsensusExpressionLevelPredicat());
	}
	
	public void testTwoNegative() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("negative"));
		evidences.add(buildEvidence("negative"));
		a.setEvidences(evidences);
        assertEquals("undetectedExpression", a.getConsensusExpressionLevelPredicat());
	}

	/*
	 * no consensus => null
	 */
	public void testNegativeAndPositive() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(buildEvidence("negative"));
		evidences.add(buildEvidence("positive"));
		a.setEvidences(evidences);
        assertEquals("detectedExpression", a.getConsensusExpressionLevelPredicat());
	}
	
	
	/*
	 * occurs for every annotation not being a :ExpressionProfile
	 */
	public void testNoExpressionLevel() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		evidences.add(new AnnotationEvidence());
		a.setEvidences(evidences);
        assertEquals(null, a.getConsensusExpressionLevelPredicat());
	}

	/*
	 * this case seems to occur (in dev at least)
	 */
	public void testNoEvidence() {
		Annotation a = new Annotation();
		List<AnnotationEvidence> evidences = new ArrayList<AnnotationEvidence>();
		a.setEvidences(evidences);
        assertEquals(null, a.getConsensusExpressionLevelPredicat());
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
