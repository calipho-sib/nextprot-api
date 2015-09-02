package org.nextprot.api.commons.constants;

public interface PropertyWriter {
	
	public String getName(); 
	public String getDataType();
	public String formatValue(String value);

}
