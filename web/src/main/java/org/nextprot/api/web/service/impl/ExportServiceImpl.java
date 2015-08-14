package org.nextprot.api.web.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.export.format.FileFormat;
import org.nextprot.api.web.NXVelocityContext;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.impl.writer.NPEntryWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ExportServiceImpl implements ExportService {

	private static final Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);
	private static final String REPOSITORY_PATH = "repository";
	private static final String[] CHROMOSOMES = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT", "unknown" };

	@Autowired
	private EntryBuilderService entryBuilderService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;
	@Autowired
	private VelocityConfig velocityConfig;
	@Autowired
	private ReleaseInfoService releaseInfoService;
	
	private int numberOfWorkers = 8;
	private ExecutorService executor = null;

	@Override
	public List<Future<File>> exportAllEntries(FileFormat format) {
		List<String> uniqueNames = new ArrayList<>();
		for (String chrm : CHROMOSOMES) {
			uniqueNames.addAll(this.masterIdentifierService.findUniqueNamesOfChromosome(chrm));
		}
		return exportEntries(uniqueNames, format);
	}

	@Override
	public List<Future<File>> exportEntriesOfChromosome(String chromossome, FileFormat format) {
		List<String> uniqueNames = this.masterIdentifierService.findUniqueNamesOfChromosome(chromossome);
		return exportEntries(uniqueNames, format);
	}

	@Override
	public List<Future<File>> exportEntries(Collection<String> uniqueNames, FileFormat format) {
		List<Future<File>> futures = new ArrayList<>();
		futures.add(exportSubPart(SubPart.HEADER, format));
		for (String uniqueName : uniqueNames) {
			futures.add(exportEntry(uniqueName, format));
		}
		futures.add(exportSubPart(SubPart.FOOTER, format));
		return futures;
	}

	@PostConstruct
	public void init() {
		executor = Executors.newFixedThreadPool(numberOfWorkers);
	}

	private Future<File> exportSubPart(SubPart part, FileFormat format) {
		return executor.submit(new ExportSubPartTask(velocityConfig.getVelocityEngine(), part, format));
	}

	@Override
	public Future<File> exportEntry(String uniqueName, FileFormat format) {
		return executor.submit(new ExportEntryTask(this.entryBuilderService, velocityConfig.getVelocityEngine(), uniqueName, format));
	}

	static class ExportEntryTask implements Callable<File> {

		private String format;
		private String filename;
		private String entryName;
		private VelocityEngine ve;
		private EntryBuilderService entryBuilderService;

		public ExportEntryTask(EntryBuilderService entryBuilderService, VelocityEngine ve, String entryName, FileFormat format) {
			this.ve = ve;
			this.entryBuilderService = entryBuilderService;
			this.filename = REPOSITORY_PATH + "/" + format.name() + "/" + entryName + "." + format.getExtension();
			//noinspection ResultOfMethodCallIgnored
			new File(filename).getParentFile().mkdirs();
			this.entryName = entryName;
			this.format = format.getExtension();
		}

		public void checkFormatConstraints() {
			if (format == null) {
				throw new RuntimeException("A format should be specified (xml or ttl)");
			}
		}

		@Override
		public File call() throws Exception {

			File f = new File(filename);
			if (f.exists()) {
				return f;
			}

			LOGGER.info("Building " + filename);

			checkFormatConstraints();
			Template template;
			VelocityContext context;
			try {

				if (format.equals(FileFormat.TURTLE.getExtension())) {
					template = ve.getTemplate("turtle/entry." + format + ".vm");
				} else {
					template = ve.getTemplate("entry." + format + ".vm");
				}

				context = new NXVelocityContext(entryBuilderService.buildWithEverything(entryName));

				FileWriter fw = new FileWriter(filename, true);
				PrintWriter out = new PrintWriter(new BufferedWriter(fw));
				template.merge(context, out);
				out.close();

			} catch (Exception e) {
				LOGGER.error("Failed to generate " + entryName + " because of " + e.getMessage());
				e.printStackTrace();
				throw e;
			}

			return f;
		}

	}

	enum SubPart {
		HEADER, FOOTER
	}

	static class ExportSubPartTask implements Callable<File> {

		private FileFormat npFormat;
		private String format;
		private VelocityEngine ve;
		private String filename;
		private SubPart part;

		public ExportSubPartTask(VelocityEngine ve, SubPart part, FileFormat format) {
			this.npFormat = format;
			this.ve = ve;
			this.part = part;
			this.format = format.getExtension();
			this.filename = REPOSITORY_PATH + "/" + format.name() + "/" + part.name() + "." + format.getExtension();
			//noinspection ResultOfMethodCallIgnored
			new File(filename).getParentFile().mkdirs();
		}

		public void checkFormatConstraints() {
			if (!(npFormat.equals(FileFormat.TURTLE) || npFormat.equals(FileFormat.XML))) {
				throw new RuntimeException("A format should be specified (xml or ttl)");
			}
		}

		@Override
		public File call() throws Exception {

			File f = new File(filename);
			if (f.exists()) {
				return f;
			}

			checkFormatConstraints();
			Template template = null;
			VelocityContext context;
			try {

				if (part.equals(SubPart.HEADER)) {
					if (format.equals(FileFormat.TURTLE.getExtension())) {
						template = ve.getTemplate("turtle/prefix.ttl.vm");
					} else {
						template = ve.getTemplate("exportStart.xml.vm");
					}
				} else if (part.equals(SubPart.FOOTER)) {
					if (format.equals(FileFormat.XML.getExtension())) {
						template = ve.getTemplate("exportEnd.xml.vm");
					}
				}

				if (template == null) {
					template = ve.getTemplate("blank.vm");
				}

				context = new VelocityContext();

				FileWriter fw = new FileWriter(filename, true);
				PrintWriter out = new PrintWriter(new BufferedWriter(fw));
				template.merge(context, out);
				out.close();

			} catch (Exception e) {
				LOGGER.error("Failed to generate header because of " + e.getMessage());
				e.printStackTrace();
				throw e;
			}

			return f;
		}
	}

	@Override
	public void clearRepository() {

		LOGGER.info("Cleaning export service repository at path: " + REPOSITORY_PATH);

		File repo = new File(REPOSITORY_PATH);

		if (repo.exists()) {

			File[] files = repo.listFiles();

			if (files != null) {
				for (File file : files) {
					//noinspection ResultOfMethodCallIgnored
					file.delete();
				}
			}
		}

	}

	@Value("${export.workers.count}")
	public void setNumberOfWorkers(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}

	@Override
	public void streamResults(NPEntryWriter writer, String viewName, List<String> accessions) throws IOException {

		Map<String, Object> map = new HashMap<>();
		map.put(ExportService.ENTRIES_COUNT_PARAM, accessions.size());
		map.put("release", releaseInfoService.findReleaseContents());

		writer.write(accessions, map);
	}
}
