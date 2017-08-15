package org.nextprot.api.web.service;

import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.solr.QueryRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public interface StreamEntryService {

	/** Stream a view of the entries into output stream in a specific format */
	void streamEntries(Collection<String> accessions, NextprotMediaType format, String viewName, OutputStream os) throws IOException;

	/** Stream all entries into output stream in a specific format */
	void streamAllEntries(NextprotMediaType format, HttpServletResponse response);

	/** Stream a view of queried entries into output stream in a specific format */
	void streamQueriedEntries(QueryRequest queryRequest, NextprotMediaType format, String viewName, HttpServletResponse response);
}
