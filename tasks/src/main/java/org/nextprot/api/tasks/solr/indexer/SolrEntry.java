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

public class SolrEntry extends SolrObject<Entry> {

    private TerminologyService terminologyservice;
    private EntryBuilderService entryBuilderService;
    private PublicationService publicationService;
    private EntryReportStatsService entryReportStatsService;
    private boolean isGold;

    private SolrEntry(Entry entry, boolean isGold) {
        super(entry);
        this.isGold = isGold;
    }

    public static SolrEntry GoldOnly(Entry entry) {

        return new SolrEntry(entry, true);
    }

    public static SolrEntry SilverAndGold(Entry entry) {

        return new SolrEntry(entry, false);
    }

	@Override
	public SolrInputDocument solrDocument() {

        Map<EntryField, FieldBuilder> fieldsBuilderMap = instanciateAllEntryFieldBuilders();

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
			fb.initializeBuilder(getDocumentType());
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

	// TODO: UGGLY CODE!!! remove those setters
	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}

    public void setPublicationService(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

	public void setEntryReportStatsService(EntryReportStatsService entryReportStatsService) {
		this.entryReportStatsService = entryReportStatsService;
	}

	static Map<EntryField, FieldBuilder> instanciateAllEntryFieldBuilders() {

        Map<EntryField, FieldBuilder> fieldsBuilderMap = new HashMap<>();
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
		return fieldsBuilderMap;
	}
}
