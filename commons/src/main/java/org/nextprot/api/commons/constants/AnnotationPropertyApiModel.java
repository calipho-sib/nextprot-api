package org.nextprot.api.commons.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnnotationPropertyApiModel {
	
	private String dbName; // name of the property in db
	private String apiName; // "base" name of property in api for both xml, json and ttl
	private String datatype;
	
	public static Map<AnnotationApiModel,Set<AnnotationPropertyApiModel>> anno2props;
	static {
		anno2props=new HashMap<AnnotationApiModel,Set<AnnotationPropertyApiModel>>();
		anno2props.put(AnnotationApiModel.PDB_MAPPING,
				new HashSet<AnnotationPropertyApiModel>(Arrays.asList(
						new AnnotationPropertyApiModel("resolution","resolution","double"), 
						new AnnotationPropertyApiModel("method"))));
		// add other links annotation - property links below
		// ...
	}
		
	public static Set<AnnotationPropertyApiModel> getPropertySet(AnnotationApiModel aModel) {
		if (anno2props.containsKey(aModel)) {
			return anno2props.get(aModel);
		} else {
			return null;
		}
	}
	
	public static AnnotationPropertyApiModel getPropertyByDbName(AnnotationApiModel aModel, String dbName) {
		if (! anno2props.containsKey(aModel)) return null;
		for (AnnotationPropertyApiModel prop: anno2props.get(aModel)) {
			if (dbName.equals(prop.dbName)) return prop;
		} 
		return null;
	}
	
	/**
	 * simplest c'tor
	 * @param dbName
	 */
	public AnnotationPropertyApiModel(String dbName) {
		this.dbName=dbName;
		this.apiName=dbName;
		this.datatype="string";
	}
	
	/**
	 * c'tor to be used when we want to manually set the property name for the api
	 * @param dbName
	 * @param apiName
	*/
	 
	public AnnotationPropertyApiModel(String dbName, String apiName,String datatype) {
		this.dbName=dbName;
		this.apiName=apiName;
		this.datatype=datatype;
	}
	
	public String getDbName() {
		return dbName;
	}
	public String getApiName() {
		return apiName;
	}		
	public String getDataType() {
		return datatype;
	}
	public String getRdfDataType() {
		return getDataType();
	}
		

}
