package org.nextprot.api.web.service.impl;

import com.google.common.base.Preconditions;
import org.apache.http.HttpStatus;
import org.nextprot.api.commons.bio.Chromosome;
import org.nextprot.api.commons.exception.ChromosomeNotFoundException;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.MasterIdentifierService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.solr.query.dto.QueryRequest;
import org.nextprot.api.web.service.SearchService;
import org.nextprot.api.web.service.StreamEntryService;
import org.nextprot.api.web.service.impl.writer.EntryFastaStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryJSONStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryPEFFStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryTTLStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryTXTStreamWriter;
import org.nextprot.api.web.service.impl.writer.EntryXLSWriter;
import org.nextprot.api.web.service.impl.writer.EntryXMLStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreamEntryServiceImpl implements StreamEntryService {

	@Autowired
	private ReleaseInfoService releaseInfoService;

	@Autowired
    private MasterIdentifierService masterIdentifierService;

	@Autowired
	private SearchService searchService;

	@Autowired
	private EntryBuilderService entryBuilderService;

	@Autowired
	private EntryReportStatsService entryReportStatsService;

	@Autowired
	private VelocityConfig velocityConfig;

	@Override
	public void streamEntry(String accession, NextprotMediaType format, OutputStream os, String description) throws IOException {

		EntryStreamWriter writer = newWriter(format, "entry", os);
		writer.write(Collections.singletonList(accession), createInfos(description));
	}

	@Override
    public void streamEntries(Collection<String> accessions, NextprotMediaType format, String viewName, OutputStream os, String description) throws IOException {

        EntryStreamWriter writer = newWriter(format, viewName, os);
        writer.write(accessions, createInfos(description));
    }

    private Map<String, Object> createInfos(String description) {
		Map<String, Object> infos = new HashMap<>();
		infos.put(EntryStreamWriter.getReleaseInfoKey(), releaseInfoService.findReleaseVersions());
		infos.put(EntryStreamWriter.getReleaseDataSourcesKey(), releaseInfoService.findReleaseDatasources());
		infos.put(EntryStreamWriter.getDescriptionKey(), description);
		return infos;
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

        List<String> accessions = sortAccessions(queryRequest);

        try {
            setResponseHeader(response, format, getFilename(queryRequest, viewName, format));

            streamEntries(accessions, format, viewName, response.getOutputStream(), getHeaderDescription(queryRequest));
        }
        catch (IOException e) {
        	throw new NextProtException(format.getExtension()+" streaming failed: cannot export "+accessions.size()+" entries (query="+queryRequest.getQuery()+")", e);
        }
    }

	private List<String> sortAccessions(QueryRequest queryRequest) {

		Set<String> accessionsSet = searchService.findAccessions(queryRequest);

		if (queryRequest.getSort() != null || queryRequest.getOrder() != null) {
			return searchService.sortAccessionsWithSolr(queryRequest, accessionsSet);
		} else {
			return accessionsSet.stream()
					.sorted()
					.collect(Collectors.toList());
		}
	}

	private String getHeaderDescription(QueryRequest queryRequest) {

		if (queryRequest.getReferer() != null && !queryRequest.getReferer().isEmpty()) {

			return queryRequest.getReferer();
		}

        String url = queryRequest.getUrl();

		if (url.contains("nextprot-api-web")) {
		    url = url.replace("/nextprot-api-web", "");
        }

        return url;
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

	/**
	 * Create new instance of streaming NPEntryWriter
	 *
	 * @param format the output file format
	 * @param view the view for velocity
	 * @param os the output stream
	 * @return a NPEntryWriter instance
	 */
	public EntryStreamWriter newWriter(NextprotMediaType format, String view, OutputStream os) throws IOException {

		Preconditions.checkNotNull(format);

		switch (format) {

			case XML:
				return new EntryXMLStreamWriter(os, view, entryBuilderService, entryReportStatsService, velocityConfig);
			case TXT:
				return new EntryTXTStreamWriter(os, entryBuilderService, entryReportStatsService, velocityConfig);
			case XLS:
				return EntryXLSWriter.newNPEntryXLSWriter(os, view, entryBuilderService, entryReportStatsService);
			case JSON:
				return new EntryJSONStreamWriter(os, view, entryBuilderService);
			case FASTA:
				return new EntryFastaStreamWriter(os, entryBuilderService, entryReportStatsService, velocityConfig);
			case PEFF:
				return new EntryPEFFStreamWriter(os, entryBuilderService, entryReportStatsService);
			case TURTLE:
				return new EntryTTLStreamWriter(os, view, entryBuilderService, entryReportStatsService, velocityConfig);
			default:
				throw new NextProtException("No NPEntryStreamWriter implementation for " + format);
		}
	}
}
