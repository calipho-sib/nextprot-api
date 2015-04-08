package org.nextprot.api.web;

import java.io.Serializable;

public class RestErrorResponse implements Serializable{

	private static final long serialVersionUID = 7820195778216393136L;
	private String message;
	private String type;
	//TODO include maven version
	private String about = "neXtProt API - https://api.nextprot.org";
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
}
