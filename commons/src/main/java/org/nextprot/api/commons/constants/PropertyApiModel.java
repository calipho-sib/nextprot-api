package org.nextprot.api.commons.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nextprot.api.commons.utils.StringUtils;

public class PropertyApiModel {
	
	private String dbName; // name of the property in db
	private String apiName; // CAMEL CASE "base" name of property in api for both xml, json and ttl
	private String datatype;
	
	public static Map<AnnotationApiModel,Set<PropertyApiModel>> anno2props;
	static {
		anno2props=new HashMap<AnnotationApiModel,Set<PropertyApiModel>>();
		anno2props.put(AnnotationApiModel.PDB_MAPPING,
				new HashSet<PropertyApiModel>(Arrays.asList(
						new PropertyApiModel("resolution","resolution","double"), 
						new PropertyApiModel("method"))));
		anno2props.put(AnnotationApiModel.PEPTIDE_MAPPING,
				new HashSet<PropertyApiModel>(Arrays.asList(
						new PropertyApiModel("peptide name","peptideName","string"), 
						new PropertyApiModel("is proteotypic", "proteotypic", "boolean"))));
		anno2props.put(AnnotationApiModel.SRM_PEPTIDE_MAPPING,
				new HashSet<PropertyApiModel>(Arrays.asList(
						new PropertyApiModel("peptide name","peptideName","string"), 
						new PropertyApiModel("is proteotypic", "proteotypic", "boolean"))));
		anno2props.put(AnnotationApiModel.BINARY_INTERACTION,
				new HashSet<PropertyApiModel>(Arrays.asList(
						new PropertyApiModel("selfInteraction","selfInteraction","boolean")))) ; 
						//new AnnotationPropertyApiModel("numberOfExperiments","numberOfExperiments","integer"))));
		
		// add other annotation - property links below
		// ...
	}
		
	public static Set<PropertyApiModel> getPropertySet(AnnotationApiModel aModel) {
		if (anno2props.containsKey(aModel)) {
			return anno2props.get(aModel);
		} else {
			return null;
		}
	}
	
	public static PropertyApiModel getPropertyByDbName(AnnotationApiModel aModel, String dbName) {
		if (! anno2props.containsKey(aModel)) return null;
		for (PropertyApiModel prop: anno2props.get(aModel)) {
			if (dbName.equals(prop.dbName)) return prop;
		} 
		return null;
	}
	
	/**
	 * simplest c'tor
	 * @param dbName
	 */
	public PropertyApiModel(String dbName) {
		this.dbName=dbName;
		this.apiName=dbName;
		this.datatype="string";
	}
	
	/**
	 * c'tor to be used when we want to manually set the property name for the api
	 * @param dbName
	 * @param apiName
	*/
	 
	public PropertyApiModel(String dbName, String apiName,String datatype) {
		this.dbName=dbName;
		this.apiName=apiName;
		this.datatype=datatype;
	}
	
	public String getDbName() {
		return dbName;
	}
		
	/*
	 * for ttl format
	 */
	public String getCamelName() {
		return apiName;
	}		

	/*
	 * for XML format
	 */
	public String getKebabName() {
		return StringUtils.camelToKebabCase(apiName);
	}		
	
	public String getDataType() {
		return datatype;
	}
	public String getRdfDataType() {
		return getDataType();
	}
	
	
	
	/** 
	 * useful to rework / format the value provided in some special cases 
	 * @param value
	 * @return
	 */
	public String formatValue(String value) {
		
		if (value!=null && datatype.equals("boolean")) {
			
			String v2 = value.toLowerCase();
			if (v2.startsWith("y") || v2.equals("1") || value.startsWith("t")) { 
				return "true"; 
			} else if (v2.startsWith("n") || v2.equals("0") || value.startsWith("f")) {
				return "false";
			} else {
				return value;
			}
			
			
		} else {
			return value; 
		}
		
	}
		

}
