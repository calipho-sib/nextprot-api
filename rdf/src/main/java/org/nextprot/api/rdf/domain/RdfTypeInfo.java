package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class RdfTypeInfo implements Serializable, Comparable<RdfTypeInfo> {

	private static final AtomicInteger counter = new AtomicInteger(5555);
	private static final long serialVersionUID = -5730184554506942284L;
	
	private String typeName; // rdf:type local name as identifier
	private String rdfsLabel;
	private String rdfsComment;
	private int instanceCount;
	private String instanceSample;
	private Set<TripleInfo> triples=new TreeSet<TripleInfo>();
	private Set<TripleInfo> parentTriples=new TreeSet<TripleInfo>();
	private Set<String> parents = new TreeSet<String>();
	private Set<String> pathToOrigin = new TreeSet<String>();

	private Set<String> values = new TreeSet<String>();
	private int id = counter.incrementAndGet();
	
	public String getInstanceSample() {
		return instanceSample;
	}

	public void setInstanceSample(String instanceSample) {
		this.instanceSample = instanceSample;
	}
	
	public void addParent(String parent) {
		this.parents.add(parent);
	}
	public Set<String> getParents() {
		return this.parents;
	}
	
	
	/**
	 * Adds a triplet type where this.typeName is the subject type
	 * @param triple
	 */
	public void addTriple(TripleInfo triple) {
		if (!triple.subjectType.equals(this.typeName)) 
			throw new RuntimeException("Cannot add triplet, expected subject type: " + typeName + " but got: " + triple.subjectType);
		triples.add(triple);		
	}

	/**
	 * 	 * Adds a triplet type where this.typeName is the object type
	 * @param triple
	 */
	public void addParentTriple(TripleInfo triple) {
		if (!triple.objectType.equals(this.typeName)) 
			throw new RuntimeException("Cannot add triplet, expected object type: " + typeName + " but got: " + triple.objectType);
		parentTriples.add(triple);		
	}

	public void addTriple(String predicate, String objectType, int tripleCount, String tripleSample) {
		TripleInfo ti = new TripleInfo();
		ti.setSubjectType(this.typeName);
		ti.setPredicate(predicate);
		ti.setObjectType(objectType);
		ti.setTripleCount(tripleCount);
		ti.setTripleSample(tripleSample);
		triples.add(ti);
	}
	
	public void addParentTriple(String predicate, String subjectType, int tripleCount, String tripleSample) {
		TripleInfo ti = new TripleInfo();
		ti.setSubjectType(subjectType);
		ti.setPredicate(predicate);
		ti.setObjectType(this.typeName); 
		ti.setTripleCount(tripleCount);
		ti.setTripleSample(tripleSample);
		parentTriples.add(ti);
	}
	
	public int getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(int instanceCount) {
		this.instanceCount = instanceCount;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setRdfsLabel(String rdfsLabel) {
		this.rdfsLabel = rdfsLabel;
	}

	public void setRdfsComment(String rdfsComment) {
		this.rdfsComment = rdfsComment;
	}

	public String getTypeName() {
		return typeName;
	}
	public String getRdfsLabel() {
		return rdfsLabel;
	}
	public String getRdfsComment() {
		return rdfsComment;
	}
	public Set<TripleInfo> getTriples() {
		return triples;
	}
	
	public Set<TripleInfo> getParentTriples() {
		return parentTriples;
	}
	
	public List<TripleInfo> findTriplesWithObjectType(String objectType) {
		List<TripleInfo> list = new ArrayList<TripleInfo>();
		for (TripleInfo ti: this.triples) 
			if (ti.objectType.equals(objectType)) 
				list.add(ti);
		return list;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("--- RdfTypeInfo ---\n");
		sb.append("typeName:" + typeName + "\n");
		sb.append("label:" + rdfsLabel + "\n");
		sb.append("comment:" + rdfsComment + "\n");
		sb.append("instanceSample:"+instanceSample + "\n");
		sb.append("instanceCount:" + instanceCount + "\n");
		for (String parent: parents) sb.append("parent: " + parent + "\n");
		for (TripleInfo t: triples ) sb.append("triple: " + t.toCsv(" - ") + "\n");
		for (TripleInfo t: parentTriples )  sb.append(" parent triple: " + t.toCsv(" - ") + "\n");
		return sb.toString();
	}
	
	public static RdfTypeInfo getExampleObject() {
		RdfTypeInfo rt = new RdfTypeInfo();
		rt.setTypeName("MyType");
		rt.setRdfsLabel("My type label");
		rt.setRdfsComment("My type comment");
		rt.setInstanceSample("mytype:Instance-34875");
		rt.setInstanceCount(33);
		rt.addTriple("predicate1", "AnotherTypeA", 18, ":Jack predicate1 :Joe");
		rt.addTriple("predicate1", "AnotherTypeB", 18, ":Jack predicate1 :Cow");
		rt.addTriple("predicate2", "AnotherTypeC", 18, ":Jack predicate2 :Apple");
		rt.addTriple("predicate3", null, 0, null);
		rt.addParentTriple("predicate22","SomeSubjectType1" , 2376, "SomeSubjectType1-instance :predicate22 SomeJack-instance");
		return rt;
	}
		
	@Override
	public int compareTo(RdfTypeInfo arg0) {
		return this.typeName.compareTo(arg0.typeName);
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

	public Set<String> getPathToOrigin() {
		return pathToOrigin;
	}

	public void setPathToOrigin(Set<String> pathToOrigin) {
		this.pathToOrigin = pathToOrigin;
	}

	public void addPathToOrigin(String pathToOrigin) {
		this.pathToOrigin.add(pathToOrigin);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
