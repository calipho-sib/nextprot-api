package org.nextprot.api.core.service.export.format;

import org.nextprot.api.commons.exception.NextProtException;

import javax.servlet.http.HttpServletRequest;

/**
 * A MIME file format http://www.freeformatter.com/mime-types-list.html
 */
public enum FileFormat {

	TXT("txt", "text/plain"),
	XLS("xls", "application/vnd.ms-excel"),
	XML("xml", "application/xml"),
	JSON("json", "application/json"),
	TURTLE("ttl", "text/turtle"),
	TSV("tsv", "text/tab-separated-values"), // https://en.wikipedia.org/wiki/Tab-separated_values
	FASTA("fasta", "text/fasta"),
	PEFF("peff", "text/peff")
	;

	private String extension;
	private String contentType;

	FileFormat(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

    public static FileFormat valueOf(HttpServletRequest request) {

        String uri = request.getRequestURI();
        if (uri.toLowerCase().endsWith(".ttl")) {
            return FileFormat.TURTLE;
        } else if (uri.toLowerCase().endsWith(".xml")) {
            return FileFormat.XML;
        } else if (uri.toLowerCase().endsWith(".json")) {
            return FileFormat.JSON;
        } else if (uri.toLowerCase().endsWith(".txt")) {
            return FileFormat.TXT;
		} else if (uri.toLowerCase().endsWith(".xls")) {
			return FileFormat.XLS;
		} else if (uri.toLowerCase().endsWith(".fasta")) {
			return FileFormat.FASTA;
        } else if (uri.toLowerCase().endsWith(".peff")) {
			return FileFormat.PEFF;
		} else if (uri.toLowerCase().endsWith(".tsv")) {
			return FileFormat.TSV;
		} else {
			throw new NextProtException(uri + ": format not recognized");
		}
    }
}
