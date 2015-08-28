package org.nextprot.api.tasks.solr.indexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;
import org.reflections.Reflections;

public class AnnotationSolrIndexer extends SolrIndexer<Entry> {

	private Map<Fields, FieldBuilder> fieldsBuilderMap = null;
	private TerminologyService terminologyservice;
	private DbXrefService dbxrefservice;

	public AnnotationSolrIndexer(String url) {
		super(url);
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Entry entry) {

		fieldsBuilderMap = new HashMap<Fields, FieldBuilder>();
		initializeFieldBuilders(fieldsBuilderMap);

		SolrInputDocument doc = new SolrInputDocument();

		for (Fields f : Fields.values()) {
			FieldBuilder fb = fieldsBuilderMap.get(f);
			fb.initializeBuilder(entry);
			Object o = fb.getFieldValue(f, f.getClazz());
			doc.addField(f.getName(), o);
		}

		//Reset all fields builders
		for (Fields f : Fields.values()) {
			fieldsBuilderMap.get(f).reset();
		}

		return doc;
	}

	public TerminologyService getTerminologysAnnotationSolrIndexerervice() {
		return terminologyservice;
	}

	public void setTerminologyservice(TerminologyService terminologyservice) {
		this.terminologyservice = terminologyservice;
	}

	public DbXrefService getDbxrefSolrIndexerervice() {
		return dbxrefservice;
	}

	public void setDbxrefservice(DbXrefService dbxrefservice) {
		this.dbxrefservice = dbxrefservice;
	}

	static void initializeFieldBuilders(Map<Fields, FieldBuilder> fieldsBuilderMap) {
		Reflections reflections = new Reflections("org.nextprot.api.tasks.solr.indexer.entry.impl");
		Set<Class<?>> entryFieldBuilderClasses = reflections.getTypesAnnotatedWith(EntryFieldBuilder.class);
		for (Class<?> c : entryFieldBuilderClasses) {
			try {
				FieldBuilder fb = (FieldBuilder) c.newInstance();
				if(fb.getSupportedFields() != null){
					for (Fields f : fb.getSupportedFields()) {
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
