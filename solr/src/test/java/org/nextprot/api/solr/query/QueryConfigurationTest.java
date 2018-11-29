package org.nextprot.api.solr.query;

import org.junit.Test;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.core.impl.settings.SortConfig;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.nextprot.api.solr.query.SolrCoreQueryTest.buildEntrySolrCore;

public class QueryConfigurationTest {

	@Test
	public void testConvertingQueryInAllModesAllValidSorts() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("kant", EnumSet.of(EntrySolrField.ID), true);

		Set<SortConfig.Criteria> crits = new HashSet<>(Arrays.asList(SortConfig.Criteria.values()));
		crits.remove(SortConfig.Criteria.NAME);

		for (QueryMode mode : QueryMode.values()) {

			for (SortConfig.Criteria crit : crits) {
				Query<EntrySolrField> query = new Query<>(core).sort(crit);
				query.setQueryMode(mode);

				query.convertToSolrQuery();
			}
		}
	}

	@Test(expected = QueryConfiguration.MissingSortConfigException.class)
	public void testConvertingQueryInSimpleModeInvalidSortByName() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("kant", EnumSet.of(EntrySolrField.ID), true);
		convertToSolrQuery(core, QueryMode.SIMPLE, SortConfig.Criteria.NAME);
	}

	@Test(expected = QueryConfiguration.MissingSortConfigException.class)
	public void testConvertingQueryInAutoCompleteModeInvalidSortByName() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("kant", EnumSet.of(EntrySolrField.ID), true);
		convertToSolrQuery(core, QueryMode.AUTOCOMPLETE, SortConfig.Criteria.NAME);
	}

	@Test(expected = QueryConfiguration.MissingSortConfigException.class)
	public void testConvertingQueryInPLModeInvalidSortByName() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("kant", EnumSet.of(EntrySolrField.ID), true);
		convertToSolrQuery(core, QueryMode.PROTEIN_LIST_SEARCH, SortConfig.Criteria.NAME);
	}

	@Test(expected = QueryConfiguration.MissingSortConfigException.class)
	public void testConvertingQueryInIDModeInvalidSortByName() throws QueryConfiguration.MissingSortConfigException {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("kant", EnumSet.of(EntrySolrField.ID), true);
		convertToSolrQuery(core, QueryMode.ID_SEARCH, SortConfig.Criteria.NAME);
	}

	private void convertToSolrQuery(SolrCore<EntrySolrField> core, QueryMode mode, SortConfig.Criteria criteria) throws QueryConfiguration.MissingSortConfigException {

		Query<EntrySolrField> query = new Query<>(core).sort(criteria);
		query.setQueryMode(mode);
		query.convertToSolrQuery();
	}
}