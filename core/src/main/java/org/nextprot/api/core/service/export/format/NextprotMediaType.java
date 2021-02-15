package org.nextprot.api.core.service.export.format;

import org.nextprot.api.commons.exception.NextProtException;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

/**
 * A MIME file format http://www.freeformatter.com/mime-types-list.html
 */
public enum NextprotMediaType {

	TXT("txt", MediaType.TEXT_PLAIN_VALUE),
	XLS("xls", NextprotMediaType.XLS_MEDIATYPE_VALUE),
	XML("xml", MediaType.APPLICATION_XML_VALUE),
	JSON("json", MediaType.APPLICATION_JSON_VALUE),
	TURTLE("ttl", NextprotMediaType.TURTLE_MEDIATYPE_VALUE),
	TSV("tsv", NextprotMediaType.TSV_MEDIATYPE_VALUE), // https://en.wikipedia.org/wiki/Tab-separated_values
	FASTA("fasta", NextprotMediaType.FASTA_MEDIATYPE_VALUE),
	PEFF("peff", NextprotMediaType.PEFF_MEDIATYPE_VALUE),
	SPLOG("splog", NextprotMediaType.SPLOG_MEDIATYPE_VALUE)
	;

	public final static String TSV_MEDIATYPE_VALUE = "text/tab-separated-values";
	public final static String TURTLE_MEDIATYPE_VALUE = "text/turtle";
	public final static String FASTA_MEDIATYPE_VALUE = "text/fasta";
	public final static String PEFF_MEDIATYPE_VALUE = "text/peff";
	public final static String XLS_MEDIATYPE_VALUE = "application/vnd.ms-excel";
	public final static String SPLOG_MEDIATYPE_VALUE = "text/plain";

	private String extension;
	private String contentType;

	NextprotMediaType(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

    public static NextprotMediaType valueOf(HttpServletRequest request) {

        String uri = request.getRequestURI();

        if (uri.contains(".")) {
			if (uri.toLowerCase().endsWith(".ttl")) {
				return NextprotMediaType.TURTLE;
			} else if (uri.toLowerCase().endsWith(".xml")) {
				return NextprotMediaType.XML;
			} else if (uri.toLowerCase().endsWith(".json")) {
				return NextprotMediaType.JSON;
			} else if (uri.toLowerCase().endsWith(".txt")) {
				return NextprotMediaType.TXT;
			} else if (uri.toLowerCase().endsWith(".xls")) {
				return NextprotMediaType.XLS;
			} else if (uri.toLowerCase().endsWith(".fasta")) {
				return NextprotMediaType.FASTA;
			} else if (uri.toLowerCase().endsWith(".peff")) {
				return NextprotMediaType.PEFF;
			} else if (uri.toLowerCase().endsWith(".tsv")) {
				return NextprotMediaType.TSV;
			} else if (uri.toLowerCase().endsWith(".splog")) {
				return NextprotMediaType.SPLOG;
			} else {
				throw new NextProtException(uri + ": format not recognized");
			}
		}
		return NextprotMediaType.TXT;
    }
}
