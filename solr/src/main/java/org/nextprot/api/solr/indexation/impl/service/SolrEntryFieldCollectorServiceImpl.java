package org.nextprot.api.solr.indexation.impl.service;

import com.google.common.base.Preconditions;
import org.apache.solr.common.SolrInputDocument;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.indexation.SolrEntryFieldCollectorService;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.AnnotationSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.CVSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ChromosomeSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.EntrySolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.ExpressionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.FilterAndPropertiesFieldsCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.IdentifierSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.InteractionSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.NamesSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.OverviewSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.PeptideSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.PublicationsSolrFieldCollector;
import org.nextprot.api.solr.indexation.impl.solrdoc.entrydoc.XrefSolrFieldCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SolrEntryFieldCollectorServiceImpl implements SolrEntryFieldCollectorService {

	@Autowired
	private AnnotationSolrFieldCollector annotationSolrFieldCollector;

	@Autowired
	private ChromosomeSolrFieldCollector chromosomeSolrFieldCollector;

	@Autowired
	private CVSolrFieldCollector cvSolrFieldCollector;

	@Autowired
	private ExpressionSolrFieldCollector expressionSolrFieldCollector;

	@Autowired
	private FilterAndPropertiesFieldsCollector filterAndPropertiesFieldsCollector;

	@Autowired
	private IdentifierSolrFieldCollector identifierSolrFieldCollector;

	@Autowired
	private InteractionSolrFieldCollector interactionSolrFieldCollector;

	@Autowired
	private NamesSolrFieldCollector namesSolrFieldCollector;

	@Autowired
	private OverviewSolrFieldCollector overviewSolrFieldCollector;

	@Autowired
	private PeptideSolrFieldCollector peptideSolrFieldCollector;

	@Autowired
	private PublicationsSolrFieldCollector publicationsSolrFieldCollector;

	@Autowired
	private XrefSolrFieldCollector xrefSolrFieldCollector;

	private Map<EntrySolrField, EntrySolrFieldCollector> collectors = new HashMap<>();
	private List<EntrySolrFieldCollector> list = new ArrayList<>();

	@PostConstruct
	public void putCollectorsInMap() {

		addCollector(annotationSolrFieldCollector);
		addCollector(chromosomeSolrFieldCollector);
		addCollector(cvSolrFieldCollector);
		addCollector(expressionSolrFieldCollector);
		addCollector(filterAndPropertiesFieldsCollector);
		addCollector(identifierSolrFieldCollector);
		addCollector(interactionSolrFieldCollector);
		addCollector(namesSolrFieldCollector);
		addCollector(overviewSolrFieldCollector);
		addCollector(peptideSolrFieldCollector);
		addCollector(publicationsSolrFieldCollector);
		addCollector(xrefSolrFieldCollector);
	}

	public SolrEntryFieldCollectorServiceImpl() { }

	/** This alternative constructor should be used in test - just add the collectors manually (mocked or real ones)
	 */
	SolrEntryFieldCollectorServiceImpl(List<EntrySolrFieldCollector> collectors) {

		Preconditions.checkNotNull(collectors);
		Preconditions.checkArgument(!collectors.isEmpty(), "missing collectors");

		collectors.forEach(this::addCollector);

		// check that each field is handled by one collector only
		list.addAll(collectors);
	}

	private void addCollector(EntrySolrFieldCollector collector) {

		for (EntrySolrField indexedField : collector.getCollectedFields()) {
			NPreconditions.checkTrue(!(collectors.containsKey(indexedField)),
					"The field " + indexedField.getName() + " cannot be retained by several collectors: " +
							indexedField.getClass() + ", " + collectors.get(indexedField));
			collectors.put(indexedField, collector);
		}
	}

	@Override
	public SolrInputDocument buildSolrDoc(String entryAccession, boolean isGoldOnly) {

		Preconditions.checkNotNull(entryAccession, "entry accession shoud not be undefined");
		Preconditions.checkArgument(!entryAccession.isEmpty(), "entry accession shoud not be empty");

		SolrInputDocument solrInputDocument = new SolrInputDocument();

		Map<EntrySolrField, Object> fields = new HashMap<>();

		// 1. collect everything from all collectors
		for (EntrySolrFieldCollector collector : list) {

			collector.collect(fields, entryAccession, isGoldOnly);
		}

		// 2. set solrInputDocument with map
		fields.keySet().forEach(esf -> solrInputDocument.addField(esf.getName(), fields.get(esf)));

		return solrInputDocument;
	}
}
