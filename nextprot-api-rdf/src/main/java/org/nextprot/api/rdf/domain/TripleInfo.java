package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.util.Set;

public class TripleInfo implements Serializable, Comparable<TripleInfo> {

	private static final long serialVersionUID = 4048949837160108934L;
	String subjectType;
	String predicate;
	String objectType;
	int tripleCount;
	String tripleSample;
	boolean literalType;
	
	private Set<String> values = null;
	
	public int getTripleCount() {
		return tripleCount;
	}
	public void setTripleCount(int tripleCount) {
		this.tripleCount = tripleCount;
	}
	public String getTripleSample() {
		return tripleSample;
	}
	public void setTripleSample(String tripleSample) {
		this.tripleSample = tripleSample;
	}
	public String getSubjectType() {
		return subjectType;
	}
	public void setSubjectType(String subjectType) {
		this.subjectType = subjectType;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("--- TripleInfo ---\n");
		sb.append("subjectType:" + subjectType + "\n");
		sb.append("predicate:" + predicate + "\n");
		sb.append("objectType:" + objectType + "\n");
		sb.append("tripleSample:"+tripleSample + "\n");
		sb.append("tripleCount:" + tripleCount + "\n");
		return sb.toString();
	}
	
	public String toCsv(String sep) {
		return subjectType + sep + predicate + sep + objectType + sep + tripleCount + sep + tripleSample;
	}

	protected String getId() {
		return subjectType+"/"+predicate+"/"+objectType;
	}
	
	@Override
	public int compareTo(TripleInfo o) {
		return this.getId().compareTo(o.getId());
	}
	
	
	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

	public void addValue(String value) {
		this.values.add(value);
	}

	
	public boolean isLiteralType() {
		return literalType;
	}
	public void setLiteralType(boolean primitiveType) {
		this.literalType = primitiveType;
	}
	
}
