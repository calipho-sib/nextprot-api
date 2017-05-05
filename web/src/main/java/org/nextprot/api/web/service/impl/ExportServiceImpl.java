package org.nextprot.api.web.service.impl;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.ChromosomeReportService;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.export.ChromosomeReportWriter;
import org.nextprot.api.core.service.export.format.NextprotMediaType;
import org.nextprot.api.web.NXVelocityContext;
import org.nextprot.api.web.service.ExportService;
import org.nextprot.api.web.service.impl.writer.EntryStreamWriter;
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
	@Autowired
	private ChromosomeReportService chromosomeReportService;
	
	private int numberOfWorkers = 8;
	private ExecutorService executor = null;

	@Override
	public List<Future<File>> exportAllEntries(NextprotMediaType format) {
		List<String> uniqueNames = new ArrayList<>();
		for (String chrm : CHROMOSOMES) {
			uniqueNames.addAll(this.masterIdentifierService.findUniqueNamesOfChromosome(chrm));
		}
		return exportEntries(uniqueNames, format);
	}

	@Override
	public List<Future<File>> exportEntriesOfChromosome(String chromossome, NextprotMediaType format) {
		List<String> uniqueNames = this.masterIdentifierService.findUniqueNamesOfChromosome(chromossome);
		return exportEntries(uniqueNames, format);
	}

	@Override
	public List<Future<File>> exportEntries(Collection<String> uniqueNames, NextprotMediaType format) {
		List<Future<File>> futures = new ArrayList<>();
		futures.add(exportSubPart(SubPart.HEADER, format));
		for (String uniqueName : uniqueNames) {
			futures.add(exportEntry(uniqueName, format));
		}
		futures.add(exportSubPart(SubPart.FOOTER, format));
		return futures;
	}

	@Override
	public void exportChromosomeEntryReport(String chromosome, NextprotMediaType nextprotMediaType, OutputStream os) throws IOException {

		Optional<ChromosomeReportWriter> writer = ChromosomeReportWriter.valueOf(nextprotMediaType, os);

		if (writer.isPresent()) {
			writer.get().write(chromosomeReportService.reportChromosome(chromosome));
		}
		else {
			throw new NextProtException("cannot export chromosome "+chromosome+": " + "unsupported "+nextprotMediaType+" format");
		}
	}

	@PostConstruct
	public void init() {
		executor = Executors.newFixedThreadPool(numberOfWorkers);
	}

	private Future<File> exportSubPart(SubPart part, NextprotMediaType format) {
		return executor.submit(new ExportSubPartTask(velocityConfig.getVelocityEngine(), part, format));
	}

	@Override
	public Future<File> exportEntry(String uniqueName, NextprotMediaType format) {
		return executor.submit(new ExportEntryTask(this.entryBuilderService, velocityConfig.getVelocityEngine(), uniqueName, format));
	}

	private static class ExportEntryTask implements Callable<File> {

		private String format;
		private String filename;
		private String entryName;
		private EntryBuilderService entryBuilderService;
		private final Template template;

		ExportEntryTask(EntryBuilderService entryBuilderService, VelocityEngine ve, String entryName, NextprotMediaType format) {

			Preconditions.checkNotNull(format, "A format should be specified (xml or ttl)");

			this.entryBuilderService = entryBuilderService;
			this.filename = REPOSITORY_PATH + "/" + format.name() + "/" + entryName + "." + format.getExtension();
			//noinspection ResultOfMethodCallIgnored
			new File(filename).getParentFile().mkdirs();
			this.entryName = entryName;
			this.format = format.getExtension();

			if (this.format.equals(NextprotMediaType.TURTLE.getExtension())) {
				template = ve.getTemplate("turtle/entry." + format + ".vm");
			} else {
				template = ve.getTemplate("entry." + format + ".vm");
			}
		}

		@Override
		public File call() throws Exception {

			File f = new File(filename);
			if (f.exists()) {
				return f;
			}

			LOGGER.info("Building " + filename);

			VelocityContext context = new NXVelocityContext(entryBuilderService.buildWithEverything(entryName));

			try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
				template.merge(context, pw);
			} catch (Exception e) {
				LOGGER.error("Failed to generate " + entryName + " because of " + e.getMessage());
				throw e;
			}

			return f;
		}

	}

	private enum SubPart {
		HEADER, FOOTER
	}

	private static class ExportSubPartTask implements Callable<File> {

		private NextprotMediaType npFormat;
		private String format;
		private VelocityEngine ve;
		private String filename;
		private SubPart part;

		private ExportSubPartTask(VelocityEngine ve, SubPart part, NextprotMediaType format) {
			this.npFormat = format;
			this.ve = ve;
			this.part = part;
			this.format = format.getExtension();
			this.filename = REPOSITORY_PATH + "/" + format.name() + "/" + part.name() + "." + format.getExtension();
			//noinspection ResultOfMethodCallIgnored
			new File(filename).getParentFile().mkdirs();
		}

		private void checkFormatConstraints() {
			if (!(npFormat.equals(NextprotMediaType.TURTLE) || npFormat.equals(NextprotMediaType.XML))) {
				throw new NextProtException("A format should be specified (xml or ttl)");
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

			if (part.equals(SubPart.HEADER)) {
				if (format.equals(NextprotMediaType.TURTLE.getExtension())) {
					template = ve.getTemplate("turtle/prefix.ttl.vm");
				} else {
					template = ve.getTemplate("exportStart.xml.vm");
				}
			} else if (part.equals(SubPart.FOOTER)) {
				if (format.equals(NextprotMediaType.XML.getExtension())) {
					template = ve.getTemplate("exportEnd.xml.vm");
				}
			}

			if (template == null) {
				template = ve.getTemplate("blank.vm");
			}

			context = new VelocityContext();

			try (FileWriter fw = new FileWriter(filename, true); PrintWriter out = new PrintWriter(new BufferedWriter(fw))) {
				template.merge(context, out);
			} catch (Exception e) {
				LOGGER.error("Failed to generate header because of " + e.getMessage());
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
	public void streamResults(EntryStreamWriter writer, String viewName, List<String> accessions) throws IOException {

		Map<String, Object> map = new HashMap<>();
		map.put(ExportService.ENTRIES_COUNT_PARAM, accessions.size());
		map.put("release", releaseInfoService.findReleaseInfo());

		writer.write(accessions, map);
	}
}
