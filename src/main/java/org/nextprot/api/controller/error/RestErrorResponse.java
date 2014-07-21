package org.nextprot.api.controller.error;

public class RestErrorResponse {

	private String message;
	private String type;
/*	private String documentationLink = "http://crick:8080/";
	private String exampleLink = "http://crick:8080/nextprot-api/entry/NX_P01308/overview.xml";*/
	private String about = "neXtProt API v0.1 - http://nextprot.org";

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/*
	public String getDocumentationLink() {
		return documentationLink;
	}

	public void setDocumentationLink(String documentationLink) {
		this.documentationLink = documentationLink;
	}

	public String getExampleLink() {
		return exampleLink;
	}

	public void setExampleLink(String exampleLink) {
		this.exampleLink = exampleLink;
	}*/


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
