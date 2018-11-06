package org.nextprot.api.solr;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.index.CvCore;
import org.nextprot.api.solr.index.GoldAndSilverEntryCore;
import org.nextprot.api.solr.index.GoldOnlyEntryCore;
import org.nextprot.api.solr.index.PublicationCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Lazy
@Repository
public class SolrCoreRepository {
	
	private Map<String, SolrCore> solrCores = new HashMap<>();

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
			solrCores.put(solrCore.getName(), solrCore);
		}
		else {
			throw new SearchConfigException("Missing schema for solr core "+solrCore.getName());
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
	
	public Collection<SolrCore> getSolrCores() {
		return Collections.unmodifiableCollection(this.solrCores.values());
	}
	
	public boolean hasSolrCore(String solrCoreName) {
		return solrCores.containsKey(solrCoreName);
	}
}
