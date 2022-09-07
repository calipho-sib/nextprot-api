package org.nextprot.api.commons.constants;

import java.util.*;

public class PropertyApiModel {
	
	protected String dbName; // name of the property in db
	protected String apiName; // CAMEL CASE "base" name of property in api for both xml, json and ttl
	protected String datatype;
	protected boolean isVisibleInXML;
	protected boolean isVisibleInTtl;
	protected Parent parent;

	// annotation properties
	public static final String NAME_INTERACTANT="interactant";
	public static final String NAME_DIFFERING_SEQUENCE="differing sequence";	
	public static final String NAME_COFACTOR="cofactor";
	public static final String NAME_ANTIBODY_NAME="antibody name";
	public static final String NAME_PEPTIDE_NAME="peptide name";
	public static final String NAME_PEPTIDE_PROTEOTYPICITY="is proteotypic";
	public static final String NAME_ALTERNATIVE_DISEASE_TERM="alternative disease description";
	public static final String NAME_RESOLUTION="resolution";
	public static final String NAME_METHOD="method";
	public static final String NAME_SELF_INTERACTION="selfInteraction";
	public static final String NAME_ANTIBODY_UNICITY="antibody unicity";
	public static final String NAME_PEPTIDE_UNICITY="peptide unicity";
	public static final String NAME_PEPTIDE_SET="peptideSet";
	public static final String NAME_PEPTIDE_UNICITY_WITH_VARIANTS="peptide unicity with variants";
	

	public static final String VALUE_TYPE_RIF="resource-internal-ref";
	public static final String VALUE_TYPE_ENTRY_AC="entry-accession";
	public static final String VALUE_TYPE_ISO_AC="isoform-accession";
	
	
	// annotation evidence properties
	public static final String NAME_GO_QUALIFIER = "go_qualifier";
	public static final String NAME_EXPRESSION_LEVEL="expressionLevel";
	public static final String NAME_EXPRESSION_SCORE="expressionScore";
	public static final String NAME_ANTIBODIES_ACC="antibodies acc";
	public static final String NAME_NUMBER_EXPERIMENTS="numberOfExperiments";
	public static final String NAME_PSIMI_AC="psimiAC";
	public static final String NAME_PSIMI_CV_NAME="psimiCvName";
	
	public static final String NAME_CELL_LINE="CL";
	public static final String NAME_INTENSITY="intensity";
	
	public static final String NAME_ALLELE_COUNT = "allele count";
	public static final String NAME_ALLELE_NUMBER = "allele number";
	public static final String NAME_HOMOZYGOTE_COUNT = "homozygote count";
	public static final String NAME_ALLELE_FREQUENCY = "allele frequency";

	
	private enum Parent { EVIDENCE, ANNOTATION }
	
	private static Map<AnnotationCategory,Set<PropertyApiModel>> anno2props;
	static {
		anno2props=new EnumMap<>(AnnotationCategory.class);

		anno2props.put(AnnotationCategory.VARIANT,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_HOMOZYGOTE_COUNT, "homozygoteCount", "integer", true, true, Parent.EVIDENCE),
						new PropertyApiModel(NAME_ALLELE_NUMBER, "alleleNumber", "integer", true, true, Parent.EVIDENCE),
						new PropertyApiModel(NAME_ALLELE_COUNT, "alleleCount", "integer", true, true, Parent.EVIDENCE),
						new PropertyApiModel(NAME_ALLELE_FREQUENCY, "alleleFrequency", "double", true, true, Parent.EVIDENCE)
						)));
		
		
		anno2props.put(AnnotationCategory.GLYCOSYLATION_SITE,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_CELL_LINE,"cellLine","string", true, true, Parent.EVIDENCE)))); 

		anno2props.put(AnnotationCategory.GO_MOLECULAR_FUNCTION,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_GO_QUALIFIER,"goQualifier","string", true, true, Parent.EVIDENCE)))); 
		anno2props.put(AnnotationCategory.GO_CELLULAR_COMPONENT,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_GO_QUALIFIER,"goQualifier","string", true, true, Parent.EVIDENCE)))); 
		
		anno2props.put(AnnotationCategory.EXPRESSION_PROFILE,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_EXPRESSION_LEVEL,"expressionLevel","string", true, true, Parent.EVIDENCE), 
						new PropertyApiModel(NAME_EXPRESSION_SCORE,"expressionScore","double", true, true, Parent.EVIDENCE)))); 
		anno2props.put(AnnotationCategory.PDB_MAPPING,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_RESOLUTION,"resolution","double", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_METHOD, "method", "string", true, true, Parent.ANNOTATION))));
		
		anno2props.put(AnnotationCategory.PEPTIDE_MAPPING,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_PEPTIDE_NAME,"peptideName","string", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_PEPTIDE_UNICITY, "peptideUniqueness","string", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_PEPTIDE_SET,"peptideSource","string", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_PEPTIDE_PROTEOTYPICITY, "proteotypic", "boolean", true, true, Parent.ANNOTATION))));
		
		anno2props.put(AnnotationCategory.SRM_PEPTIDE_MAPPING,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_PEPTIDE_NAME,"peptideName","string", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_PEPTIDE_UNICITY,"peptideUniqueness","string", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_PEPTIDE_PROTEOTYPICITY, "proteotypic", "boolean", true, true, Parent.ANNOTATION))));
		
		anno2props.put(AnnotationCategory.ANTIBODY_MAPPING,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_ANTIBODY_NAME, "antibodyName", "string", true, true, Parent.ANNOTATION),
						new PropertyApiModel(NAME_ANTIBODY_UNICITY, "antibodyUniqueness", "string", true, true, Parent.ANNOTATION)
						)));
		
		anno2props.put(AnnotationCategory.BINARY_INTERACTION,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_SELF_INTERACTION,"selfInteraction","boolean", true, true, Parent.ANNOTATION), 
						new PropertyApiModel(NAME_PSIMI_AC, "interactionDetectionMethod","string", true, true, Parent.EVIDENCE), 
						new PropertyApiModel(NAME_NUMBER_EXPERIMENTS,"numberOfExperiments","integer", true, true, Parent.EVIDENCE))));
		
		anno2props.put(AnnotationCategory.INTERACTION_MAPPING,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_PSIMI_AC, "interactionDetectionMethod","string", true, true, Parent.EVIDENCE))));

		anno2props.put(AnnotationCategory.PHENOTYPIC_VARIATION,
				new HashSet<>(Arrays.asList(
						new PropertyApiModel(NAME_INTENSITY,"intensity","string", false, true, Parent.EVIDENCE)))); 
		// add other annotation - property links below
		// ...
	}

	
	public static PropertyWriter getXMLWriter(AnnotationCategory aModel, String propertyDbName) {
		if (! anno2props.containsKey(aModel)) return null;
		for (PropertyApiModel prop: anno2props.get(aModel)) {
			if (propertyDbName.equals(prop.dbName)) {
				return new PropertyXMLWriter(prop);
			}
		} 
		return null;
	}

	public static PropertyWriter getTtlWriter(AnnotationCategory aModel, String propertyDbName) {
		if (! anno2props.containsKey(aModel)) return null;
		for (PropertyApiModel prop: anno2props.get(aModel)) {
			if (propertyDbName.equals(prop.dbName)) {
				return new PropertyTtlWriter(prop);
			}
		} 
		return null;
	}
	
	
	protected PropertyApiModel(String dbName, String apiName,String datatype, boolean isVisibleInXML, boolean isVisibleInTtl, Parent parent) {
		this.dbName=dbName;
		this.apiName=apiName;
		this.datatype=datatype;
		this.isVisibleInTtl=isVisibleInTtl;
		this.isVisibleInXML=isVisibleInXML;
		this.parent=parent;
	}

	

	
	/** 
	 * useful to rework / format the value provided in some special cases 
	 * @param value
	 * @return
	 */
	protected String formatValue(String value) {

		if (value==null) return null;
		
		if ("boolean".equals(datatype)) return toTrueFalse(value);

		if (dbName.equals(PropertyApiModel.NAME_EXPRESSION_LEVEL)) {
			switch (value) {
				case "high" : return "High";
				case "low"  : return "Low";
				case "medium" : return "Medium";
				case "not detected" : return "Negative";
				case "positive" : return "Positive";
				case "negative" : return "Negative";
				case "detected" : return "Positive";
				default: throw new RuntimeException("Invalid value " + value + " for property "+ PropertyApiModel.NAME_EXPRESSION_LEVEL );
			}
		}
		
		if (dbName.equals(PropertyApiModel.NAME_PEPTIDE_SET)) {
			switch (value) {
				case "PeptideAtlas human phosphoproteome" : return "PeptideAtlas_human_phosphoproteome";
				default: return value;
			}
		}
		
		if (dbName.equals(PropertyApiModel.NAME_PSIMI_AC)) {
			return value.replace(":", "_");
		}
		
		// default
		return value; 
	}

	protected String toTrueFalse(String value) {
		String v2 = value.toLowerCase();
		if (v2.startsWith("y") || "1".equals(v2) || value.startsWith("t")) {
			return "true"; 
		} else if (v2.startsWith("n") || "0".equals(v2) || value.startsWith("f")) {
			return "false";
		} else {
			return value;
		}
	}
	
}





