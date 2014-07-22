package org.nextprot.api.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.nextprot.core.exception.SearchConfigException;
import org.springframework.stereotype.Component;

@Component
public class SolrConfiguration {
	
	private Map<String, SolrIndex> indexes = new HashMap<String, SolrIndex>(); 
	private List<Class<? extends SolrIndex>> indexClasses;
	
	@PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		SolrIndex instance = null;
		for(Class<? extends SolrIndex> clazz : this.indexClasses) {
			instance = clazz.newInstance();
			addIndex(instance);
		}
	}

	public void setIndexes(List<Class<? extends SolrIndex>> indexClasses) {
		this.indexClasses = indexClasses;
	}
	
	private void addIndex(SolrIndex index) {
		if (index.getFields() != null && (index.getFields().isMemberClass() || index.getFields().isEnum()))
			this.indexes.put(index.getName(), index);
		else throw new SearchConfigException("Didn't setup fields for index "+index.getName());
	}
	
	/**
	 * 
	 * @param indexName
	 * @return
	 */
	public SolrIndex getIndexByName(String indexName) {
		if(this.indexes.containsKey(indexName))
			return this.indexes.get(indexName);
		else throw new SearchConfigException("Index "+indexName+" does not exist. Available indexes: "+this.indexes.entrySet());  
	}
	
	public List<SolrIndex> getIndexes() {
		return new ArrayList<SolrIndex>(this.indexes.values());
	}
	
	public boolean hasIndex(String indexName) {
		return this.indexes.containsKey(indexName);
	}
}
