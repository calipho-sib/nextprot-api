package org.nextprot.api.commons.constants;



public class PropertyTtlWriter extends PropertyApiModel implements PropertyWriter {

	public PropertyTtlWriter(PropertyApiModel model) {
		super(model.dbName, model.apiName, model.datatype, model.isVisibleInXML,  model.isVisibleInTtl, model.parent);
	}
	
	public String getName() {
		return apiName; // we assume the member value is a camel string
	}
	
	public String getDataType() {
		return datatype;
	}
	
	public String formatValue(String value) {
		// the values of these are URIs
		if (dbName.equals(NAME_INTEGRATION_LEVEL) || dbName.equals(NAME_EXPRESSION_LEVEL)) {
			return ":" + super.formatValue(value);
		} else if (dbName.equals(NAME_PSIMI_AC)) {
			return "cv:" + super.formatValue(value);
		}
		// by default values are literal with a known data type
		return "\"" + super.formatValue(value) + "\"^^xsd:" + datatype; 
	}
}
