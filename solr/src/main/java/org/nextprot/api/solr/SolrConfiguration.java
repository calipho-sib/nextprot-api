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
	
	private Map<String, SolrCore> solrCores = new HashMap<>();
	private List<Class<? extends SolrCore>> solrCoreClasses;

	@PostConstruct
	public void buildSolrCores() throws InstantiationException, IllegalAccessException {
		for(Class<? extends SolrCore> clazz : this.solrCoreClasses) {
			addSolrCore(clazz.newInstance());
		}
	}

	public void setSolrCores(List<Class<? extends SolrCore>> solrCoreClasses) {
		this.solrCoreClasses = solrCoreClasses;
	}
	
	private void addSolrCore(SolrCore solrCore) {
		if (solrCore.getFieldValues() != null && solrCore.getFieldValues().length > 0) {
			solrCores.put(solrCore.getName(), solrCore);
		}
		else {
			throw new SearchConfigException("Didn't setup fields for solr core "+solrCore.getName());
		}
	}

	public SolrCore getSolrCoreByName(String coreName) {
		if(this.solrCores.containsKey(coreName)) {
			return solrCores.get(coreName);
		}
		else {
			throw new SearchConfigException("Solr core "+coreName+" does not exist. Available cores: "+this.solrCores.entrySet());
		}
	}
	
	public List<SolrCore> getSolrCores() {
		return new ArrayList<>(this.solrCores.values());
	}
	
	public boolean hasSolrCore(String solrCoreName) {
		return solrCores.containsKey(solrCoreName);
	}
}
