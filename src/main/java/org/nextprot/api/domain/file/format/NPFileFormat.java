package org.nextprot.api.domain.file.format;

public enum NPFileFormat {

	TXT("txt", "text/plain"), 
	XML("xml", "application/xml"), 
	JSON("json", "application/json"), 
	TURTLE("ttl", "text/turtle"), 
	TSV("tsv", "text/tab-separated-values");

	private String extension;
	private String contentType;

	NPFileFormat(String extension, String contentType) {

		this.extension = extension;
		this.contentType = contentType;

	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

}
