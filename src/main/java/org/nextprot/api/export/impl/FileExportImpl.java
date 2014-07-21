package org.nextprot.api.export.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.nextprot.api.domain.Entry;
import org.nextprot.api.service.EntryService;
import org.nextprot.api.service.MasterIdentifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Export service
 * 
 * @author mpereira, dteixeira
 * 
 */
@Component
@Deprecated 
public class FileExportImpl  {

	private static final Log LOGGER = LogFactory.getLog(FileExportImpl.class);
	@Autowired
	private EntryService entryService;
	@Autowired
	private MasterIdentifierService masterIdentifierService;
	private String fileRepositoryPath;

	private VelocityEngine velocityEngine;

	private final int NUMBER_THREADS = 16;
	private final String[] CHROMOSSOMES = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "X", "Y", "MT" };

	private FileReader getEntryFile(String entryName) {

		try {

			String fileName = this.fileRepositoryPath + "export-" + entryName + ".xml";
			if (!(new File(fileName)).exists()) {
				exportEntry(entryName);
			}
			return new FileReader(fileName);

		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void clearCache() {

		if(new File(fileRepositoryPath).exists()){
			for (File file : new File(fileRepositoryPath).listFiles())
				file.delete();
		}
	}

	private void exportEntry(String entryName) {

		Template template = getTemplateAndMerge("exportEntry.xml.vm", null, null);
		ExportTask exportTask = new ExportTask(this.fileRepositoryPath, template, entryName);
		exportTask.run();

	}

	public void exportEntries(String filepath, String... entryNames) {
		try {

			OutputStream outputStream = new FileOutputStream(filepath);
			exportEntries(outputStream, entryNames);

		} catch (FileNotFoundException e) {

			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public void exportEntry(OutputStream outputStream, String entryName) {
		PrintWriter writer = new PrintWriter(outputStream);

		BufferedReader in = null;
		String line = null;

		try {

			in = new BufferedReader(getEntryFile(entryName));

			while ((line = in.readLine()) != null) {
				writer.println(line);
			}
			in.close();

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		writer.close();
	}

	public void exportEntries(OutputStream outputStream, String... entryNames) {
		PrintWriter writer = new PrintWriter(outputStream);
		VelocityContext context = new VelocityContext();

		getTemplateAndMerge("exportStart.xml.vm", context, writer);

		BufferedReader in = null;
		String line = null;

		try {

			for (String entryName : entryNames) {
				in = new BufferedReader(getEntryFile(entryName));

				while ((line = in.readLine()) != null) {
					writer.println(line);
				}
				in.close();
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		getTemplateAndMerge("exportEnd.xml.vm", context, writer);

		writer.close();
	}

	public void exportAllEntries(String filepath) {
		List<String> entries = new ArrayList<String>();
		for (String chrm : CHROMOSSOMES)
			entries.addAll(this.masterIdentifierService.findUniqueNamesOfChromossome(chrm));

		try {

			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filepath + "export.xml", true)));
			BufferedReader in = null;
			String line = null;

			VelocityContext context = new VelocityContext();
			getTemplateAndMerge("exportStart.xml.vm", context, writer);

			for (String entry : entries) {
				in = new BufferedReader(new FileReader(this.fileRepositoryPath + "export-" + entry + ".xml"));

				while ((line = in.readLine()) != null) {
					writer.println(line);
				}
				in.close();
			}
			getTemplateAndMerge("exportEnd.xml.vm", context, writer);

			writer.close();

		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void exportChromossome(OutputStream outputStream, String chromossome) {
		PrintWriter writer = new PrintWriter(outputStream);
		VelocityContext context = new VelocityContext();
		Template template;
		try {
			template = this.velocityEngine.getTemplate("exportStart.xml.vm");
			template.merge(context, writer);

			BufferedReader in = new BufferedReader(new FileReader(this.fileRepositoryPath + "chromossome-" + chromossome + ".xml"));
			String line = null;

			while ((line = in.readLine()) != null) {
				writer.println(line);
			}

			template = this.velocityEngine.getTemplate("exportEnd.xml.vm");
			template.merge(context, writer);

			in.close();
			writer.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void createEntryRepository() {
		Map<String, List<String>> chromossomeEntryMap = new HashMap<String, List<String>>();

		Template template = getTemplateAndMerge("exportEntry.xml.vm", null, null);
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);

		List<String> temp = null;
		for (String chrm : CHROMOSSOMES) {
			temp = this.masterIdentifierService.findUniqueNamesOfChromossome(chrm);
			chromossomeEntryMap.put(chrm, temp);
			for (String name : temp) {
				Runnable exportTask = new ExportTask(this.fileRepositoryPath, template, name);
				executor.execute(exportTask);
			}
		}

		// Wait for all threads to stop
		executor.shutdown();

		for (java.util.Map.Entry<String, List<String>> entry : chromossomeEntryMap.entrySet()) {
			exportEntries("chromossome-" + entry.getKey() + ".xml", entry.getValue().toArray(new String[0]));
		}
	}

	public void createChromossomeRepository() {
		List<String> entryNames = null;
		for (String chrm : CHROMOSSOMES) {
			LOGGER.info("Chromossome: " + chrm);
			entryNames = this.masterIdentifierService.findUniqueNamesOfChromossome(chrm);
			exportEntries(this.fileRepositoryPath + "chromossome-" + chrm + ".xml", entryNames.toArray(new String[0]));
		}
	}

	public void setFileRepositoryPath(String fileRepositoryPath) {
		this.fileRepositoryPath = fileRepositoryPath;
	}

	@PostConstruct
	private void init() {
		if (this.velocityEngine == null) {

			this.velocityEngine = new VelocityEngine();
			this.velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			this.velocityEngine.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
			this.velocityEngine.setProperty("file.resource.loader.path", "./src/main/webapp/WEB-INF/velocity/");

			Properties props = new Properties();
			try {
				props.load(new FileInputStream("src/main/webapp/WEB-INF/velocity/velocity.properties"));
				for(Object p : props.keySet()){
					String propertyName = (String) p;
					this.velocityEngine.setProperty(propertyName, props.getProperty(propertyName));
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
			
			try {
				this.velocityEngine.init();
			} catch (Exception e) {
				LOGGER.error("Could not initialize Velocity Engine: " + e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @author mpereira, dteixeira
	 */
	class ExportTask implements Runnable {
		private String filepath;
		private Template template;
		private String name;

		ExportTask(String filepath, Template template, String name) {
			this.filepath = filepath;
			this.template = template;
			this.name = name;
		}

		@Override
		public void run() {
			VelocityContext context = null;
			StringWriter stringWriter = null;
			String filename = null;
			PrintWriter writer = null;
			Entry entry = null;

			context = new VelocityContext();

			entry = entryService.findEntry(name);
			context.put("entry", entry);
			stringWriter = new StringWriter();

			try {
				template.merge(context, stringWriter);
				filename = this.filepath + "export-" + name + ".xml";
				FileWriter fw = new FileWriter(new File(filename), true);
				writer = new PrintWriter(new BufferedWriter(fw));
				writer.write(stringWriter.toString());
				writer.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}

	private Template getTemplateAndMerge(String templateName, VelocityContext context, PrintWriter writer) {
		try {
			Template template = this.velocityEngine.getTemplate(templateName);
			if ((context != null) && (writer != null))
				template.merge(context, writer);
			return template;
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (ParseErrorException e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

}
