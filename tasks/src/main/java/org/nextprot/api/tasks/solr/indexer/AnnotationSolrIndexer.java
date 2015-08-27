package org.nextprot.api.tasks.solr.indexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.service.DbXrefService;
import org.nextprot.api.core.service.TerminologyService;
import org.nextprot.api.solr.index.EntryIndex.Fields;
import org.nextprot.api.tasks.solr.indexer.entry.EntryFieldBuilder;
import org.nextprot.api.tasks.solr.indexer.entry.FieldBuilder;
import org.reflections.Reflections;


public class AnnotationSolrIndexer extends SolrIndexer<Entry> {
	
	private TerminologyService terminologyservice;
	private DbXrefService dbxrefservice;

	public AnnotationSolrIndexer(String url) {
		super(url);
	}

	@Override
	public SolrInputDocument convertToSolrDocument(Entry entry) {
		
		initializeFieldBuilders();
		
		SolrInputDocument doc = new SolrInputDocument();

		for(Fields f : Fields.values()){
			FieldBuilder fb = fieldsBuilderMap.get(f);
			fb.initializeBuilder(entry);
			Object o = fb.getFieldValue(f, f.getClazz());
			doc.addField(f.getName(), o);
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

	
	private Map<Fields, FieldBuilder> fieldsBuilderMap = new HashMap<Fields, FieldBuilder>();

	private void initializeFieldBuilders() {
	     Reflections reflections = new Reflections("org.nextprot.api.tasks.solr.indexer.entry.impl");
	     Set<Class<?>> entryFieldBuilderClasses = reflections.getTypesAnnotatedWith(EntryFieldBuilder.class);
	     for(Class<?> c : entryFieldBuilderClasses){
	    	 try {
				FieldBuilder fb = (FieldBuilder) c.newInstance();
				for(Fields f: fb.getSupportedFields()){
					fieldsBuilderMap.put(f, fb);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	     }
	}
	
}
