package org.nextprot.api.commons.constants;


import org.nextprot.api.commons.utils.StringUtils;

public class PropertyXMLWriter extends PropertyApiModel implements PropertyWriter {

	public PropertyXMLWriter(PropertyApiModel model) {
		super(model.dbName, model.apiName, model.datatype, model.isVisibleInXML,  model.isVisibleInTtl, model.parent);
	}
	
	public String getName() {
		return StringUtils.camelToKebabCase(apiName);
	}
	
	public String formatValue(String value) {
		if (dbName.equals(NAME_EXPRESSION_LEVEL)) return super.formatValue(value);
		if (dbName.equals(NAME_INTENSITY) && "NA".equals(value)) return null;
		return value;
	}
	
	public String getDataType() {
		return null;
	}

}
