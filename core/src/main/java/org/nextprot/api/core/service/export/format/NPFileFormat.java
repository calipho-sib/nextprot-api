package org.nextprot.api.core.service.export.format;

public enum NPFileFormat {

	TXT("txt", "text/plain", null, null), 
	XML("xml", "application/xml", "<entry-list>", "</entry-list>"), 
	JSON("json", "application/json", null, null), 
	TURTLE("ttl", "text/turtle", null, null), 
	TSV("tsv", "text/tab-separated-values", null, null);

	private String header;
	private String footer;
	private String extension;
	private String contentType;

	NPFileFormat(String extension, String contentType, String header, String footer) {
		this.extension = extension;
		this.contentType = contentType;
		this.header = header;
		this.footer = footer;
	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

	public String getHeader() {
		return header;
	}

	public String getFooter() {
		return footer;
	}


}
