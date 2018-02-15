package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.EntryReport;

import java.util.List;

public interface EntryGeneReportService {

	/**
	 * Report informations about the given entry accession
	 * @param entryAccession the neXtProt entry accession
	 * @return a list of EntryReport (an Entry can map multiple chromosomal locations)
	 */
	List<EntryReport> reportEntry(String entryAccession);
}
