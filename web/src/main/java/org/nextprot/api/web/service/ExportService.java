package org.nextprot.api.web.service;

import org.nextprot.api.core.service.export.format.FileFormat;
import org.nextprot.api.web.service.impl.writer.NPEntryStreamWriter;

import java.io.File;
import java.io.IOException;
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
	List<Future<File>> exportAllEntries(FileFormat format);

	/**
	 * Export entries based on chromosome in the format specified with UTF-8
	 * encoding
	 * 
	 * @param chromosome The chromosome name / number
	 * @param format The format can be xml or ttl
	 */
	List<Future<File>> exportEntriesOfChromosome(String chromosome, FileFormat format);

	/**
	 * Export entries based on entry names in the format specified with UTF-8
	 * encoding
	 * 
	 * @param entryNames The list of entries
	 */
	List<Future<File>> exportEntries(Collection<String> entryNames, FileFormat format);

	/**
	 * Export the entry name in the format specified with UTF-8 encoding
	 * 
	 * @param entryName The entry to export
	 * @param format The export format
	 */
	Future<File> exportEntry(String entryName, FileFormat format);

	void clearRepository();

	void streamResults(NPEntryStreamWriter writer, String viewName, List<String> accessions) throws IOException;
}
