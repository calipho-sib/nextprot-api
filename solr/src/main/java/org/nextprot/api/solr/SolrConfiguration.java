package org.nextprot.api.solr;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Component
public class SolrConfiguration {
	
	private Map<String, SolrCore> indexes = new HashMap<>();
	private List<Class<? extends SolrCore>> indexClasses;
	
	@PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		SolrCore instance;
		for(Class<? extends SolrCore> clazz : this.indexClasses) {
			instance = clazz.newInstance();
			addIndex(instance);
		}
	}

	public void setIndexes(List<Class<? extends SolrCore>> indexClasses) {
		this.indexClasses = indexClasses;
	}
	
	private void addIndex(SolrCore index) {
		if (index.getFields() != null && (index.getFields().isMemberClass() || index.getFields().isEnum()))
			this.indexes.put(index.getName(), index);
		else throw new SearchConfigException("Didn't setup fields for index "+index.getName());
	}
	
	/**
	 * 
	 * @param indexName
	 * @return
	 */
	public SolrCore getIndexByName(String indexName) {
		if(this.indexes.containsKey(indexName))
			return this.indexes.get(indexName);
		else throw new SearchConfigException("Index "+indexName+" does not exist. Available indexes: "+this.indexes.entrySet());  
	}
	
	public List<SolrCore> getIndexes() {
		return new ArrayList<SolrCore>(this.indexes.values());
	}
	
	public boolean hasIndex(String indexName) {
		return this.indexes.containsKey(indexName);
	}
}
