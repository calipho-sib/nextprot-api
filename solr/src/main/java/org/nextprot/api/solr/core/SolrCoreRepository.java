package org.nextprot.api.solr.core;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Lazy
@Repository
public class SolrCoreRepository {

	private final Map<SolrCore.Entity, SolrCore> solrCores = new HashMap<>();

	@Autowired
	private CvCore cvCore;

	@Autowired
	private GoldAndSilverEntryCore goldAndSilverEntryCore;

	@Autowired
	private GoldOnlyEntryCore goldOnlyEntryCore;

	@Autowired
	private PublicationCore publicationCore;

	@PostConstruct
	private void addSolrCores() {

		Stream.of(cvCore, goldAndSilverEntryCore, goldOnlyEntryCore, publicationCore).forEach(core -> addSolrCore(core));
	}
	
	private void addSolrCore(SolrCore solrCore) {
		if (solrCore.getSchema() != null && solrCore.getSchema().length > 0) {
			solrCores.put(solrCore.getEntity(), solrCore);
		}
		else {
			throw new SearchConfigException("Missing schema for solr core "+solrCore.getEntity());
		}
	}

	public SolrCore getSolrCore(String name) {

		return getSolrCore(SolrCore.Entity.valueOfName(name));
	}

	public SolrCore getSolrCore(SolrCore.Entity coreName) {

		if (this.solrCores.containsKey(coreName)) {
			return solrCores.get(coreName);
		}
		else {
			throw new SearchConfigException("Solr core "+coreName+" does not exist. Available cores: "+this.solrCores.entrySet());
		}
	}
	
	public boolean hasSolrCore(String entityName) {
		return solrCores.containsKey(SolrCore.Entity.valueOfName(entityName));
	}
}
