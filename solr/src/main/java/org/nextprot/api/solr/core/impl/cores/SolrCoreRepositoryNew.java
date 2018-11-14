package org.nextprot.api.solr.core.impl.cores;

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
public class SolrCoreRepositoryNew {

	private final Map<SolrCoreNew.Alias, SolrCoreNew> solrCores = new HashMap<>();

	@Autowired
	private CvCoreNew cvCore;

	@Autowired
	private GoldAndSilverEntryCoreNew goldAndSilverEntryCore;

	@Autowired
	private GoldOnlyEntryCoreNew goldOnlyEntryCore;

	@Autowired
	private PublicationCoreNew publicationCore;

	@PostConstruct
	private void addSolrCores() {

		Stream.of(cvCore, goldAndSilverEntryCore, goldOnlyEntryCore, publicationCore)
				.forEach(this::addSolrCore);
	}
	
	private void addSolrCore(SolrCoreNew solrCore) {
		if (solrCore.getSchema() != null && solrCore.getSchema().length > 0) {
			solrCores.put(solrCore.getAlias(), solrCore);
		}
		else {
			throw new SearchConfigException("Missing schema for solr core "+solrCore.getAlias());
		}
	}

	public SolrCoreNew getSolrCoreFromAlias(String aliasName) {

		return getSolrCore(SolrCoreNew.Alias.valueOfName(aliasName));
	}

	public SolrCoreNew getSolrCore(SolrCoreNew.Alias alias) {

		if (this.solrCores.containsKey(alias)) {
			return solrCores.get(alias);
		}
		else {
			throw new SearchConfigException("Solr core "+alias+" does not exist. Available cores: "+this.solrCores.entrySet());
		}
	}
	
	public boolean hasSolrCore(String aliasName) {

		return solrCores.containsKey(SolrCoreNew.Alias.valueOfName(aliasName));
	}
}
