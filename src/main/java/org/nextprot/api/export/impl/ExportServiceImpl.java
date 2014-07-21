package org.nextprot.api.export.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
import org.apache.velocity.runtime.RuntimeConstants;
import org.nextprot.api.domain.file.format.NPFileFormat;
import org.nextprot.api.export.ExportService;
import org.nextprot.api.service.AnnotationService;
import org.nextprot.api.service.DbXrefService;
import org.nextprot.api.service.EntryService;
import org.nextprot.api.service.GeneService;
import org.nextprot.api.service.IdentifierService;
import org.nextprot.api.service.IsoformService;
import org.nextprot.api.service.KeywordService;
import org.nextprot.api.service.MasterIdentifierService;
import org.nextprot.api.service.PublicationService;
import org.nextprot.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
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

	private final int NUMBER_THREADS = 8;
	private final static Log LOGGER = LogFactory.getLog(ExportServiceImpl.class);

	private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
	private static String REPOSITORY_PATH = "repository";

	private final String[] CHROMOSSOMES = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT", "unknown" };

	private VelocityEngine velocityEngine;

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
	public List<Future<File>> exportEntries(List<String> uniqueNames, NPFileFormat format) {
		List<Future<File>> futures = new ArrayList<Future<File>>();
		futures.add(exportSubPart(SubPart.HEADER, format));
		for (String uniqueName : uniqueNames) {
			futures.add(exportEntry(uniqueName, format));
		}
		futures.add(exportSubPart(SubPart.FOOTER, format));
		return futures;
	}

	private Future<File> exportSubPart(SubPart part, NPFileFormat format) {
		return executor.submit(new ExportSubPartTask(this.velocityEngine, part, format));
	}

	@Override
	public Future<File> exportEntry(String uniqueName, NPFileFormat format) {
		return executor.submit(new ExportEntryTask(this.entryService, this.velocityEngine, uniqueName, format));
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
			Template exportBody;
			VelocityContext context = null;
			try {
				if (format.equals(NPFileFormat.TURTLE.getExtension())) {
					exportBody = ve.getTemplate("turtle/entry." + format + ".vm");
				} else {
					exportBody = ve.getTemplate("entry." + format + ".vm");
				}
				
				context = new VelocityContext();
				context.put("entry", entryService.findEntry(entryName));
				context.put("StringUtils", StringUtils.class);

				FileWriter fw = new FileWriter(filename, true);
				PrintWriter out = new PrintWriter(new BufferedWriter(fw));
				exportBody.merge(context, out);
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
			Template exportBody = null;
			VelocityContext context = null;
			try {

				if (part.equals(SubPart.HEADER)) {
					if (format.equals(NPFileFormat.TURTLE.getExtension())) {
						exportBody = ve.getTemplate("turtle/prefix.ttl.vm");
					} else {
						exportBody = ve.getTemplate("exportStart.xml.vm");
					}
				} else if (part.equals(SubPart.FOOTER)) {
					if (format.equals(NPFileFormat.XML.getExtension())) {
						exportBody = ve.getTemplate("exportEnd.xml.vm");
					}
				}
				
				if(exportBody == null){
					exportBody = ve.getTemplate("blank.vm");
				}

				context = new VelocityContext();

				FileWriter fw = new FileWriter(filename, true);
				PrintWriter out = new PrintWriter(new BufferedWriter(fw));
				exportBody.merge(context, out);
				out.close();

			} catch (Exception e) {
				LOGGER.error("Failed to generate header because of " + e.getMessage());
				e.printStackTrace();
				throw e;
			}

			return f;

		}

	}

	@PostConstruct
	private void init() {

		clearRepository();

		if (this.velocityEngine == null) {

			this.velocityEngine = new VelocityEngine();
			this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			this.velocityEngine.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
			this.velocityEngine.setProperty("file.resource.loader.path", "./src/main/webapp/WEB-INF/velocity/");

			Properties props = new Properties();
			try {
				props.load(new FileInputStream("src/main/webapp/WEB-INF/velocity/velocity.properties"));
				for (Object p : props.keySet()) {
					String propertyName = (String) p;
					this.velocityEngine.setProperty(propertyName, props.getProperty(propertyName));
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			try {
				this.velocityEngine.init();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
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

}
