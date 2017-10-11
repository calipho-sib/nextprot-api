package org.nextprot.api.web.service.impl;

import org.apache.http.HttpStatus;
import org.nextprot.api.commons.bio.Chromosome;
import org.nextprot.api.commons.exception.ChromosomeNotFoundException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.solr.QueryRequest;
import org.nextprot.api.web.service.SearchService;
import org.nextprot.api.web.service.StreamEntryService;
import org.nextprot.api.web.service.impl.writer.EntryStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static org.nextprot.api.web.service.impl.writer.EntryStreamWriter.newAutoCloseableWriter;

@Service
public class StreamEntryServiceImpl implements StreamEntryService {

	@Autowired
	private ReleaseInfoService releaseInfoService;

	@Autowired
    private MasterIdentifierService masterIdentifierService;

	@Autowired
	private SearchService searchService;

	@Override
	public void streamEntry(String accession, NextprotMediaType format, OutputStream os, String description) throws IOException {

		EntryStreamWriter writer = newAutoCloseableWriter(format, "entry", os);
		writer.write(Collections.singletonList(accession), releaseInfoService.findReleaseInfo(), description);
	}

	@Override
    public void streamEntries(Collection<String> accessions, NextprotMediaType format, String viewName, OutputStream os, String description) throws IOException {

        EntryStreamWriter writer = newAutoCloseableWriter(format, viewName, os);

        writer.write(accessions, releaseInfoService.findReleaseInfo(), description);
    }

    @Override
    public void streamAllEntries(NextprotMediaType format, HttpServletResponse response) {

        try {
            setResponseHeader(response, format, "nextprot_all"  + "." + format.getExtension());
            streamEntries(masterIdentifierService.findUniqueNames(), format, "entry", response.getOutputStream(), "complete release");
        } catch (IOException e) {
            throw new NextProtException(format.getExtension()+" streaming failed: cannot export all "+masterIdentifierService.findUniqueNames().size()+" entries", e);
        }
    }

    @Override
    public void streamAllChromosomeEntries(String chromosome, NextprotMediaType format, HttpServletResponse response) {

		if (!Chromosome.exists(chromosome)) {

			ChromosomeNotFoundException ex = new ChromosomeNotFoundException(chromosome);
			response.setStatus(HttpStatus.SC_NOT_FOUND);
			try {
				response.getWriter().print(ex.getMessage());
			} catch (IOException e) {
				throw new NextProtException(format.getExtension() + " streaming failed: "+ex.getMessage(), e);
			}
		}
		else {
			try {
				setResponseHeader(response, format, "nextprot_chromosome_" + chromosome + "." + format.getExtension());
				streamEntries(masterIdentifierService.findUniqueNamesOfChromosome(chromosome), format, "entry", response.getOutputStream(), "chromosome " + chromosome);
			} catch (IOException e) {
				throw new NextProtException(format.getExtension() + " streaming failed: cannot export all " + masterIdentifierService.findUniqueNames().size() + " entries from chromosome " + chromosome, e);
			}
		}
    }

    @Override
    public void streamQueriedEntries(QueryRequest queryRequest, NextprotMediaType format, String viewName, HttpServletResponse response) {

        List<String> entries = getAccessions(queryRequest);

        try {
            setResponseHeader(response, format, getFilename(queryRequest, viewName, format));

            streamEntries(entries, format, viewName, response.getOutputStream(), getHeaderDescription(queryRequest));
        }
        catch (IOException e) {
        	throw new NextProtException(format.getExtension()+" streaming failed: cannot export "+entries.size()+" entries (query="+queryRequest.getQuery()+")", e);
        }
    }

	private List<String> getAccessions(QueryRequest queryRequest) {

		Set<String> accessionsSet = searchService.getAccessions(queryRequest);
		List<String> accessions;

		if (queryRequest.getSort() != null || queryRequest.getOrder() != null) {
			//TODO This is very slow and is highly memory intensive please review the way of sorting this using only the asking for ids. See the SearchServiceTest
			accessions = searchService.sortAccessions(queryRequest, accessionsSet);
		} else {
			accessions = new ArrayList<>(accessionsSet);
			Collections.sort(accessions);
		}

		return accessions;
	}

	private String getHeaderDescription(QueryRequest queryRequest) {

		if (queryRequest.getReferer() != null && !queryRequest.getReferer().isEmpty()) {

			return queryRequest.getReferer();
		}

		return queryRequest.getUrl();
	}

	private void setResponseHeader(HttpServletResponse response, NextprotMediaType format, String filename) {

		if (!format.equals(NextprotMediaType.JSON)) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
		}
	}

	private String getFilename(QueryRequest queryRequest, String viewName, NextprotMediaType format) {

		if (queryRequest.hasNextProtQuery()) {
			return "nextprot-query-" + queryRequest.getQueryId() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.hasList()) {
			return "nextprot-list-" + queryRequest.getListId() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.getQuery() != null) { // search and add filters
			return "nextprot-search-" + queryRequest.getQuery() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.getSparql() != null) { // search and add filters
			return "nextprot-sparql-" + queryRequest.getSparql() + "-" + viewName + "." + format.getExtension();
		} else if (queryRequest.getChromosome() != null) { // search and add filters
			return "nextprot-chromosome-" + queryRequest.getChromosome() + "-" + viewName + "." + format.getExtension();
		} else {
			throw new NextProtException("Not implemented yet.");
		}
	}
}
