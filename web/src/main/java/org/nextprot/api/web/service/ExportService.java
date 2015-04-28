package org.nextprot.api.web.service;

import org.nextprot.api.core.service.export.format.NPFileFormat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

public interface ExportService {

	/**
	 * Export all entries in the format specified with UTF-8 encoding
	 * 
	 * @param format The format can be xml or ttl
	 */
	List<Future<File>> exportAllEntries(NPFileFormat format);

	/**
	 * Export entries based on chromosome in the format specified with UTF-8
	 * encoding
	 * 
	 * @param chromosome The chromosome name / number
	 * @param format The format can be xml or ttl
	 */
	public List<Future<File>> exportEntriesOfChromossome(String chromosome, NPFileFormat format);

	/**
	 * Export entries based on entry names in the format specified with UTF-8
	 * encoding
	 * 
	 * @param entryNames The list of entries
	 */
	public List<Future<File>> exportEntries(Collection<String> entryNames, NPFileFormat format);

	/**
	 * Export the entry name in the format specified with UTF-8 encoding
	 * 
	 * @param entryName The entry to export
	 * @param format The export format
	 */
	public Future<File> exportEntry(String entryName, NPFileFormat format);

	public void clearRepository();

	void streamResults(NPFileFormat format, Writer stream, String viewName, List<String> accessions) throws IOException;
}
