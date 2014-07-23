package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.util.Date;

import org.jsondoc.core.annotation.ApiObject;

@ApiObject(name = "Version", description = "source version that generate triples")
public class Version implements Serializable{

	private static final long serialVersionUID = -7029978352134310155L;

	private String name="unknow";
	private String version="unknow";
	private String date;
	public Version() {
		date=new Date().toLocaleString();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate(){
		return date;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion(){
		return version;
	}
}
