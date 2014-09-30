package org.nextprot.api.core.domain;

import java.io.Serializable;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "identifier", description = "The identifier")
public class Identifier implements Serializable{

	private static final long serialVersionUID = 5134635208466189617L;

	@ApiObjectField(description = "The identifier name")
	private String name;
	@ApiObjectField(description = "The identifier type")
	private String type;
	@ApiObjectField(description = "The identifier database")
	private String database;
	
/*
	private String id;
	private Long synonymId;
	private Long xrefId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getSynonymId() {
		return synonymId;
	}
	public void setSynonymId(Long syn_id) {
		this.synonymId = syn_id;
	}
	public Long getXrefId() {
		return xrefId;
	}
	public void setXrefId(Long xref_id) {
		this.xrefId = xref_id;
	}
*/
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
	
	
	/**
	 * Returns a string specifying the provenance of the identifier
	 * @return the database if not null otherwise returns the type
	 */
	public String getProvenance() {
		if (database!=null) return database;
		return type;
	}
	
	
}
