package org.nextprot.api.core.service.export.format;

import org.nextprot.api.commons.exception.NextProtException;

import javax.servlet.http.HttpServletRequest;

/**
 * A file format
 */
public enum NPFileFormat {

	TXT("txt", "text/plain"),
	XLS("xls", "application/vnd.ms-excel"),
	XML("xml", "application/xml"),
	JSON("json", "application/json"),
	TURTLE("ttl", "text/turtle"),
	TSV("tsv", "text/tab-separated-values"),
	FASTA("fasta", "text/fasta"),
	PEFF("peff", "text/peff")
	;

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

    public static NPFileFormat valueOf(HttpServletRequest request) {

        String uri = request.getRequestURI();
        if (uri.toLowerCase().endsWith(".ttl")) {
            return NPFileFormat.TURTLE;
        } else if (uri.toLowerCase().endsWith(".xml")) {
            return NPFileFormat.XML;
        } else if (uri.toLowerCase().endsWith(".json")) {
            return NPFileFormat.JSON;
        } else if (uri.toLowerCase().endsWith(".txt")) {
            return NPFileFormat.TXT;
		} else if (uri.toLowerCase().endsWith(".xls")) {
			return NPFileFormat.XLS;
		} else if (uri.toLowerCase().endsWith(".fasta")) {
			return NPFileFormat.FASTA;
        } else if (uri.toLowerCase().endsWith(".peff")) {
			return NPFileFormat.PEFF;
		} else
            throw new NextProtException("Format not recognized");
    }
}
