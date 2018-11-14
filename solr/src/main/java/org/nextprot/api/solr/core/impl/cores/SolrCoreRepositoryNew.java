package org.nextprot.api.solr.core.impl.cores;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.CvCore;
import org.nextprot.api.solr.core.impl.GoldOnlyEntryCore;
import org.nextprot.api.solr.core.impl.PublicationCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Lazy
@Repository
public class SolrCoreRepositoryNew {

	private final Map<SolrCore.Alias, SolrCore> solrCores = new HashMap<>();

	@Autowired
	private CvCore cvCore;

	@Autowired
	private SolrCore goldAndSilverEntryCore;

	@Autowired
	private GoldOnlyEntryCore goldOnlyEntryCore;

	@Autowired
	private PublicationCore publicationCore;

	@PostConstruct
	private void addSolrCores() {

		Stream.of(cvCore, goldAndSilverEntryCore, goldOnlyEntryCore, publicationCore)
				.forEach(this::addSolrCore);
	}
	
	private void addSolrCore(SolrCore solrCore) {
		if (solrCore.getSchema() != null && solrCore.getSchema().length > 0) {
			solrCores.put(solrCore.getAlias(), solrCore);
		}
		else {
			throw new SearchConfigException("Missing schema for solr core "+solrCore.getAlias());
		}
	}

	public SolrCore getSolrCoreFromAlias(String aliasName) {

		return getSolrCore(SolrCore.Alias.valueOfName(aliasName));
	}

	public SolrCore getSolrCore(SolrCore.Alias alias) {

		if (this.solrCores.containsKey(alias)) {
			return solrCores.get(alias);
		}
		else {
			throw new SearchConfigException("Solr core "+alias+" does not exist. Available cores: "+this.solrCores.entrySet());
		}
	}
	
	public boolean hasSolrCore(String aliasName) {

		return solrCores.containsKey(SolrCore.Alias.valueOfName(aliasName));
	}
}
