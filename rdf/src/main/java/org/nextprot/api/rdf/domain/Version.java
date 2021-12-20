package org.nextprot.api.rdf.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsondoc.core.annotation.ApiObject;

@ApiObject(name = "Version", description = "source version that generate triples")
public class Version implements Serializable{

	private static final long serialVersionUID = -7029978352134310155L;

	private String name="unknown";
	private String version="unknown";
	private Date date;
	public Version() {
		date=new Date();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDate() {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		return sdf.format(this.date);
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getVersion(){
		return version;
	}
}
