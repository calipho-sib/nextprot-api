package org.nextprot.api.web.service;

import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.web.service.impl.writer.EntryStreamWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

public interface ExportService {

	/**
	 * Params name to show the number of entries (used on velocity templates)
	 */
	String ENTRIES_COUNT_PARAM = "entriesCount";
	
	/**
	 * Export all entries in the format specified with UTF-8 encoding
	 * 
	 * @param format The format can be xml or ttl
	 */
	List<Future<File>> exportAllEntries(NextprotMediaType format);

	/**
	 * Export entries based on chromosome in the format specified with UTF-8
	 * encoding
	 * 
	 * @param chromosome The chromosome name / number
	 * @param format The format can be xml or ttl
	 */
	List<Future<File>> exportEntriesOfChromosome(String chromosome, NextprotMediaType format);

	/**
	 * Export entries based on entry names in the format specified with UTF-8
	 * encoding
	 * 
	 * @param entryNames The list of entries
	 */
	List<Future<File>> exportEntries(Collection<String> entryNames, NextprotMediaType format);

	/**
	 * Export the list of gene/neXtProt entries informations found on the given chromosome
	 * @param chromosome the chromosome to get report
	 * @param nextprotMediaType the export file format
	 * @param os the output stream to write into
	 */
	void exportChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, OutputStream os) throws IOException;

	/**
	 * Export the entry name in the format specified with UTF-8 encoding
	 * 
	 * @param entryName The entry to export
	 * @param format The export format
	 */
	Future<File> exportEntry(String entryName, NextprotMediaType format);

	void clearRepository();

	void streamResults(EntryStreamWriter writer, String viewName, List<String> accessions) throws IOException;
}
