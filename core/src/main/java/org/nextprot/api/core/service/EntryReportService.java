package org.nextprot.api.core.service;

import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.EntryReport;
import org.nextprot.api.core.domain.annotation.AnnotationEvidence;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface EntryReportService {

	/**
	 * Report informations about the given entry accession
	 * @param entryAccession the neXtProt entry accession
	 * @return a list of EntryReport (an Entry can map multiple chromosomal locations)
	 */
	List<EntryReport> reportEntry(String entryAccession);

	boolean entryIsNAcetyled(Entry entry, Predicate<AnnotationEvidence> isExperimentalPredicate);

	boolean entryIsPhosphorylated(Entry entry, Predicate<AnnotationEvidence> isExperimentalPredicate);

	Map<String, String> reportIsoformPeffHeaders(String entryAccession);
}
