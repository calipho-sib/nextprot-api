package org.nextprot.api.tasks.solr.indexer;

import com.google.common.collect.Sets;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.solr.index.EntryField;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SolrEntry extends SolrObject<Entry> {

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

        Map<EntryField, EntryFieldBuilder> fieldsBuilderMap = instanciateAllEntryFieldBuilders();

		SolrInputDocument doc = new SolrInputDocument();

		for (EntryField f : EntryField.values()) {
			//System.err.println("field: " + f.toString());
			if(f == EntryField.TEXT || f == EntryField.SCORE) continue; // Directly computed by SOLR
			EntryFieldBuilder fb = fieldsBuilderMap.get(f);
			fb.setGold(isGold);
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

	static Map<EntryField, EntryFieldBuilder> instanciateAllEntryFieldBuilders() {

        Map<EntryField, EntryFieldBuilder> fieldsBuilderMap = new HashMap<>();
		Reflections reflections = new Reflections("org.nextprot.api.tasks.solr.indexer.entry.impl");

		Set<Class<?>> entryFieldBuilderClasses = Sets.newHashSet(); //reflections.getTypesAnnotatedWith(EntryFieldBuilder.class);
		for (Class<?> c : entryFieldBuilderClasses) {
			try {
				EntryFieldBuilder fb = (EntryFieldBuilder) c.newInstance();

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
