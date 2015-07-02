package org.nextprot.api.web.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.core.service.AnnotationService;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.EntryService;
import org.nextprot.api.core.service.GeneService;
import org.nextprot.api.core.service.IdentifierService;
import org.nextprot.api.core.service.IsoformService;
import org.nextprot.api.core.service.KeywordService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.ReleaseInfoService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.web.NXVelocityContext;
import org.nextprot.api.web.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

@Service
@Lazy
public class ExportServiceImpl implements ExportService {

	@Autowired
	private PublicationService publicationService;
	@Autowired
	private DbXrefService xrefService;
	@Autowired
	private KeywordService kwService;
	@Autowired
	private IdentifierService identifierService;
	@Autowired
	private GeneService geneService;
	@Autowired
	private IsoformService isoformService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;
	@Autowired
	private AnnotationService annotationService;
	@Autowired
	private EntryService entryService;
	@Autowired
	private FluentEntryService fluentEntryService;
	@Autowired
	private VelocityConfig velocityConfig;
	@Autowired
	private TerminologyService terminologyService;
	@Autowired
    private ReleaseInfoService releaseInfoService;

	private int numberOfWorkers = 8;

	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

	private ExecutorService executor = null;

	private static String REPOSITORY_PATH = "repository";

	private final String[] CHROMOSSOMES = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT", "unknown" };

	/*
	 * @Autowired private VelocityConfig config;
	 */

	@Override
	public List<Future<File>> exportAllEntries(NPFileFormat format) {
		List<String> uniqueNames = new ArrayList<String>();
		for (String chrm : CHROMOSSOMES) {
			uniqueNames.addAll(this.masterIdentifierService.findUniqueNamesOfChromossome(chrm));
		}
		return exportEntries(uniqueNames, format);
	}

	@Override
	public List<Future<File>> exportEntriesOfChromossome(String chromossome, NPFileFormat format) {
		List<String> uniqueNames = this.masterIdentifierService.findUniqueNamesOfChromossome(chromossome);
		return exportEntries(uniqueNames, format);
	}

	@Override
	public List<Future<File>> exportEntries(Collection<String> uniqueNames, NPFileFormat format) {
		List<Future<File>> futures = new ArrayList<Future<File>>();
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

	private Future<File> exportSubPart(SubPart part, NPFileFormat format) {
		return executor.submit(new ExportSubPartTask(velocityConfig.getVelocityEngine(), part, format));
	}

	@Override
	public Future<File> exportEntry(String uniqueName, NPFileFormat format) {
		return executor.submit(new ExportEntryTask(this.entryService, velocityConfig.getVelocityEngine(), uniqueName, format));
	}

	static class ExportEntryTask implements Callable<File> {

		private String format;
		private String filename;
		private String entryName;
		private VelocityEngine ve;
		private EntryService entryService;

		public ExportEntryTask(EntryService entryService, VelocityEngine ve, String entryName, NPFileFormat format) {
			this.ve = ve;
			this.entryService = entryService;
			this.filename = REPOSITORY_PATH + "/" + format.name() + "/" + entryName + "." + format.getExtension();
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
			VelocityContext context = null;
			try {

				if (format.equals(NPFileFormat.TURTLE.getExtension())) {
					template = ve.getTemplate("turtle/entry." + format + ".vm");
				} else {
					template = ve.getTemplate("entry." + format + ".vm");
				}

				context = new NXVelocityContext(entryService.findEntry(entryName));

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
	};

	static class ExportSubPartTask implements Callable<File> {

		private NPFileFormat npFormat;
		private String format;
		private VelocityEngine ve;
		private String filename;
		private SubPart part;

		public ExportSubPartTask(VelocityEngine ve, SubPart part, NPFileFormat format) {
			this.npFormat = format;
			this.ve = ve;
			this.part = part;
			this.format = format.getExtension();
			this.filename = REPOSITORY_PATH + "/" + format.name() + "/" + part.name() + "." + format.getExtension();
			new File(filename).getParentFile().mkdirs();
		}

		public void checkFormatConstraints() {
			if (!(npFormat.equals(NPFileFormat.TURTLE) || npFormat.equals(NPFileFormat.XML))) {
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
			VelocityContext context = null;
			try {

				if (part.equals(SubPart.HEADER)) {
					if (format.equals(NPFileFormat.TURTLE.getExtension())) {
						template = ve.getTemplate("turtle/prefix.ttl.vm");
					} else {
						template = ve.getTemplate("exportStart.xml.vm");
					}
				} else if (part.equals(SubPart.FOOTER)) {
					if (format.equals(NPFileFormat.XML.getExtension())) {
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
		if (new File(REPOSITORY_PATH).exists()) {
			for (File file : new File(REPOSITORY_PATH).listFiles())
				file.delete();
		}

	}

	public int getNumberOfWorkers() {
		return numberOfWorkers;
	}

	@Value("${export.workers.count}")
	public void setNumberOfWorkers(int numberOfWorkers) {
		this.numberOfWorkers = numberOfWorkers;
	}

	@Override
	public void streamResults(NPFileFormat format, Writer stream, String viewName, List<String> accessions) throws IOException {

		NPStreamExporter exporter = NPFileExporter.valueOf(format).getNPStreamExporter();

		exporter.setTerminologyService(terminologyService);
		
		if(format.equals(NPFileFormat.XML)){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("entriesCount", accessions.size());
			map.put("release", releaseInfoService.findReleaseContents());
			exporter.export(accessions, stream, viewName, map);
		}else exporter.export(accessions, stream, viewName, null);
	}
}
