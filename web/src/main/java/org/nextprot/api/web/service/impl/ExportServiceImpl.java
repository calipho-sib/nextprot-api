package org.nextprot.api.web.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.service.MasterIdentifierService;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.*;
import org.nextprot.api.core.service.export.format.NPFileFormat;
import org.nextprot.api.core.service.fluent.FluentEntryService;
import org.nextprot.api.core.utils.NXVelocityUtils;
import org.nextprot.api.web.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.velocity.VelocityConfig;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
	private VelocityConfig config;

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
		return executor.submit(new ExportSubPartTask(config.getVelocityEngine(), part, format));
	}

	@Override
	public Future<File> exportEntry(String uniqueName, NPFileFormat format) {
		return executor.submit(new ExportEntryTask(this.entryService, config.getVelocityEngine(), uniqueName, format));
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

				context = new VelocityContext();
				context.put("entry", entryService.findEntry(entryName));
				context.put("StringUtils", StringUtils.class);
				context.put("NXUtils", NXVelocityUtils.class);

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
	public void streamResultsInXML(Writer writer, String viewName, List<String> accessions, boolean withHeader, boolean withFooter) {
		try {

			if (withHeader) {
				writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
				//writer.write("<nextprot-export xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"https://dl.dropboxusercontent.com/u/2037400/nextprot-export.xsd\">\n");
				writer.write("<nextprot-export xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
				// TODO add <header>
				writer.write("<entry-list>\n");
				writer.flush();
			}

			if (accessions != null) {
				for (String acc : accessions) {
					streamXml(acc, viewName, writer);
					writer.flush();
				}
			}

			if (withFooter) {
				writer.write("</entry-list>\n");
				writer.write("</nextprot-export>\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Failed to stream xml");
		}
	}

	@Override
	public void streamResultsInJson(Writer writer, String viewName, List<String> accessions) {
		JsonGenerator generator = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory factory = mapper.getFactory();
			generator = factory.createGenerator(writer);

			for (String acc : accessions) {
				streamJson(acc, viewName, generator);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (generator != null) {
				try {
					generator.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void streamResultsInFasta(Writer writer, String viewName, List<String> accessions) {

		try {
			if (accessions != null) {
				for (String acc : accessions) {
					streamFasta(acc, writer);
					writer.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new NextProtException("Failed to stream fasta");
		}

	}

	private void streamXml(String entryName, String viewName, Writer writer) {

		Template template = config.getVelocityEngine().getTemplate("entry.xml.vm");

		streamWithTemplate(template, entryName, writer, viewName);
	}

	private void streamFasta(String entryName, Writer writer) {

		Template template = config.getVelocityEngine().getTemplate("fasta/entry.fasta.vm");

		streamWithTemplate(template, entryName, writer, "isoform", "overview");
	}

	private void streamWithTemplate(Template template, String entryName, Writer writer, String... viewNames) {

		FluentEntryService.FluentEntry fluentEntry = fluentEntryService.newFluentEntry(entryName);

		for (String viewName : viewNames)
			fluentEntry.buildWithView(viewName);

		VelocityContext context = new VelocityContext();
		context.put("entry", fluentEntry.build());
		context.put("StringUtils", StringUtils.class);
		context.put("NXUtils", NXVelocityUtils.class);

		template.merge(context, writer);
	}

	private void streamJson(String entryName, String viewName, JsonGenerator generator) throws IOException {

		Entry entry = fluentEntryService.newFluentEntry(entryName).buildWithView(viewName);
		generator.writeObject(entry);
	}

}
