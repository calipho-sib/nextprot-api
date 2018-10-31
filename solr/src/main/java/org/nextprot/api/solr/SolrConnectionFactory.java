package org.nextprot.api.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.nextprot.api.commons.exception.SearchConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Component
public class SolrConnectionFactory {
	private Map<String, HttpSolrServer> serverMap;
	private final String baseSolrUrl;
	private final List<SolrCore> indexes;
	
	private final char DASH = '/';
	
	@Autowired
	public SolrConnectionFactory(final SolrConfiguration solrConfiguration, @Value("${solr.url}") final String baseSolrUrl) {
		indexes = solrConfiguration.getIndexes();

		if(baseSolrUrl.charAt(baseSolrUrl.length()-1) != DASH)
			this.baseSolrUrl = baseSolrUrl + DASH;
		else this.baseSolrUrl = baseSolrUrl;
		
	}
	
	public String getSolrBaseUrl() {
		return this.baseSolrUrl;
	}
	
	public SolrServer getServer(String indexName) {

		//optimise starting up
		synchronized (this) {
			if(serverMap == null){
				initializeServerMap();
			}
		}
		
		if(this.serverMap.containsKey(indexName)) 
			return this.serverMap.get(indexName);
		else throw new SearchConfigException("Index "+indexName+" is not available");

		
	}
	
	

	/**
	 * Initialize the map only when needed to optimise the start up of the program
	 */
	private void initializeServerMap(){
		this.serverMap = new HashMap<String, HttpSolrServer>();
		for(SolrCore index : indexes) {
			this.serverMap.put(index.getName(), new HttpSolrServer(this.baseSolrUrl+index.getUrl()));
		}
	}
	
	
}
