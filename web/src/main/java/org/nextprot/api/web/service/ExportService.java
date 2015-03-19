package org.nextprot.api.web.service;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.nextprot.api.core.service.export.format.NPFileFormat;

public interface ExportService {

	/**
	 * Export all entries in the format specified with UTF-8 encoding
	 * 
	 * @param filepath
	 *            The name of the file where to save
	 * @param format
	 *            The format can be xml or ttl
	 */
	List<Future<File>> exportAllEntries(NPFileFormat format);

	/**
	 * Export entries based on chromosome in the format specified with UTF-8
	 * encoding
	 * 
	 * @param chromosome
	 *            The chromosome name / number
	 * @param filepath
	 *            The name of the file where to save
	 * @param format
	 *            The format can be xml or ttl
	 */
	public List<Future<File>> exportEntriesOfChromossome(String chromosome, NPFileFormat format);

	/**
	 * Export entries based on entry names in the format specified with UTF-8
	 * encoding
	 * 
	 * @param entryNames
	 *            The list of entries
	 * @param filepath
	 *            The format can be xml or ttl
	 */
	public List<Future<File>> exportEntries(Collection<String> entryNames, NPFileFormat format);

	/**
	 * Export the entry name in the format specified with UTF-8 encoding
	 * 
	 * @param entryNames
	 *            The list of entries
	 * @param filepath
	 *            The format can be xml or ttl
	 */
	public Future<File> exportEntry(String entryName, NPFileFormat format);

	public void clearRepository();

	void streamResultsInXML(Writer writer, String viewName, Set<String> accessions, boolean close);

	void streamResultsInJson(Writer writer, String viewName, Set<String> accessions, boolean close);


}
