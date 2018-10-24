package org.nextprot.api.tasks.solr.indexer;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.EntryBuilderService;
import org.nextprot.api.core.service.EntryReportStatsService;
import org.nextprot.api.core.service.PublicationService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EntryBaseSolrIndexer extends SolrIndexer<Entry> {

	private Map<EntryField, FieldBuilder> fieldsBuilderMap = null;
	private TerminologyService terminologyservice;
	private EntryBuilderService entryBuilderService;
	private PublicationService publicationService;
	private EntryReportStatsService entryReportStatsService;
    private boolean isGold;

	// protected => only sub classes can use c'tor (was abstract class before)
	protected EntryBaseSolrIndexer(String url, boolean isGold) {
		super(url);
		this.isGold = isGold;
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Entry entry) {

		fieldsBuilderMap = new HashMap<>();
		initializeFieldBuilders(fieldsBuilderMap);

		SolrInputDocument doc = new SolrInputDocument();

		for (EntryField f : EntryField.values()) {
			//System.err.println("field: " + f.toString());
			if(f == EntryField.TEXT || f == EntryField.SCORE) continue; // Directly computed by SOLR
			FieldBuilder fb = fieldsBuilderMap.get(f);
			fb.setGold(isGold);
			fb.setTerminologyService(terminologyservice);
			fb.setEntryBuilderService(entryBuilderService);
			fb.setPublicationService(publicationService);
			fb.setEntryReportStatsService(entryReportStatsService);
			fb.initializeBuilder(entry);
			Object o = fb.getFieldValue(f, f.getClazz());
			doc.addField(f.getName(), o);
		}

		//Reset all fields builders
		for (EntryField f : EntryField.values()) {
			if(f == EntryField.TEXT || f == EntryField.SCORE) continue; // Directly computed by SOLR
			fieldsBuilderMap.get(f).reset();
		}

		return doc;
	}

	public void setEntryBuilderService(EntryBuilderService entryBuilderService) {
		this.entryBuilderService = entryBuilderService;
	}

	public EntryBuilderService getEntryBuilderService() {
		return entryBuilderService;
	}

	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}

    public void setPublicationService(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

	public void setEntryReportStatsService(EntryReportStatsService entryReportStatsService) {
		this.entryReportStatsService = entryReportStatsService;
	}

	static void initializeFieldBuilders(Map<EntryField, FieldBuilder> fieldsBuilderMap) {
		Reflections reflections = new Reflections("org.nextprot.api.tasks.solr.indexer.entry.impl");
		Set<Class<?>> entryFieldBuilderClasses = reflections.getTypesAnnotatedWith(EntryFieldBuilder.class);
		for (Class<?> c : entryFieldBuilderClasses) {
			try {
				FieldBuilder fb = (FieldBuilder) c.newInstance();

				if(fb.getSupportedFields() != null){
					for (EntryField f : fb.getSupportedFields()) {
						NPreconditions.checkTrue(!(fieldsBuilderMap.containsKey(f)), "The field " + f.getName() + " is supported by several builders: " + fb.getClass() + ", " + fieldsBuilderMap.get(f));
						fieldsBuilderMap.put(f, fb);
					}
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	

}
