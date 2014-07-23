package org.nextprot.api.core.domain;

import java.io.Serializable;


/**
 * Represents main entity name or synonym of a given isoform entry.
 * @author dteixeira
 *
 */
public class IsoformEntityName implements Serializable{

	private static final long serialVersionUID = 1326005085947657873L;
	private String mainEntityName; 
	private String type;
	private String qualifier;
	private String value;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQualifier() {
		return qualifier;
	}
	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getMainEntityName() {
		return mainEntityName;
	}
	public void setMainEntityName(String mainEntityName) {
		this.mainEntityName = mainEntityName;
	}
	

}
