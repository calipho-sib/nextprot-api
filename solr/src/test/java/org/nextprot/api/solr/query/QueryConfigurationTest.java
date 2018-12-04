package org.nextprot.api.solr.query;

import org.junit.Assert;
import org.junit.Test;
import org.nextprot.api.solr.core.SolrCore;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.impl.SolrCvCore;
import org.nextprot.api.solr.core.impl.SolrPublicationCore;
import org.nextprot.api.solr.core.impl.schema.EntrySolrField;
import org.nextprot.api.solr.core.impl.settings.SortConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.nextprot.api.solr.query.SolrCoreQueryTest.buildEntrySolrCore;

public class QueryConfigurationTest {

	@Test
	public void shouldConvertQueryIn3ModesAllValidCritsInEntrySolrCore() {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("roudoudou", EnumSet.of(EntrySolrField.ID), false);

		Set<SortConfig.Criteria> crits = new HashSet<>(Arrays.asList(SortConfig.Criteria.values()));
		crits.remove(SortConfig.Criteria.NAME);

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, EnumSet.of(QueryMode.SIMPLE, QueryMode.AUTOCOMPLETE, QueryMode.PROTEIN_LIST_SEARCH), crits);
		Assert.assertTrue(exceptions.isEmpty());
	}

	@Test
	public void shouldNotConvertQueryInAllModesAllInvalidCritsInEntrySolrCore() {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("roudoudou", EnumSet.of(EntrySolrField.ID), false);

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, new HashSet<>(Arrays.asList(QueryMode.values())), EnumSet.of(SortConfig.Criteria.NAME));
		Assert.assertEquals(4, exceptions.size());
	}

	@Test
	public void shouldConvertQueryIn3ModesAllValidCritsInEntryGoldSolrCore() {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("roudoudou", EnumSet.of(EntrySolrField.ID), true);

		Set<SortConfig.Criteria> crits = new HashSet<>(Arrays.asList(SortConfig.Criteria.values()));
		crits.remove(SortConfig.Criteria.NAME);

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, EnumSet.of(QueryMode.SIMPLE, QueryMode.AUTOCOMPLETE, QueryMode.PROTEIN_LIST_SEARCH), crits);
		Assert.assertTrue(exceptions.isEmpty());
	}

	@Test
	public void shouldConvertQueryInIDModeAllValidCritsInEntryGoldSolrCore() {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("roudoudou", EnumSet.of(EntrySolrField.ID), true);

		List<QueryConfiguration.MissingSortConfigException> exceptions = convertToSolrQuery(core, EnumSet.of(QueryMode.ID_SEARCH), EnumSet.of(SortConfig.Criteria.SCORE));
		Assert.assertTrue(exceptions.isEmpty());
	}

	@Test
	public void shouldShouldNotConvertQueryInIDModeInvalidCritsInEntryGoldSolrCore() {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("roudoudou", EnumSet.of(EntrySolrField.ID), true);

		Set<SortConfig.Criteria> crits = new HashSet<>(Arrays.asList(SortConfig.Criteria.values()));
		crits.remove(SortConfig.Criteria.NAME);

		List<QueryConfiguration.MissingSortConfigException> exceptions = convertToSolrQuery(core, EnumSet.of(QueryMode.ID_SEARCH), crits);
		Assert.assertEquals(6, exceptions.size());
	}

	@Test
	public void shouldNotConvertQueryInAllModesAllInvalidCritsInEntryGoldSolrCore() {

		SolrCore<EntrySolrField> core = buildEntrySolrCore("roudoudou", EnumSet.of(EntrySolrField.ID), true);

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, new HashSet<>(Arrays.asList(QueryMode.values())), EnumSet.of(SortConfig.Criteria.NAME));
		Assert.assertEquals(4, exceptions.size());
	}

	@Test
	public void shouldConvertQueryInAllModesAllValidCritsInSolrCvCore() {

		SolrCvCore core = new SolrCvCore("roudoudou");

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, EnumSet.of(QueryMode.SIMPLE, QueryMode.AUTOCOMPLETE),
						EnumSet.of(SortConfig.Criteria.NAME, SortConfig.Criteria.SCORE));

		Assert.assertTrue(exceptions.isEmpty());
	}

	@Test
	public void shouldNotConvertQueryInAllModesAllInvalidCritsInSolrCvCore() {

		SolrCvCore core = new SolrCvCore("roudoudou");

		Set<SortConfig.Criteria> badCrits = new HashSet<>(Arrays.asList(SortConfig.Criteria.values()));
		badCrits.remove(SortConfig.Criteria.NAME);
		badCrits.remove(SortConfig.Criteria.SCORE);

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, EnumSet.of(QueryMode.SIMPLE, QueryMode.AUTOCOMPLETE), badCrits);

		Assert.assertEquals(12, exceptions.size());
	}

	@Test
	public void shouldConvertQueryInAllModesAllValidCritsSolrPublicationCore() {

		SolrPublicationCore core = new SolrPublicationCore("roudoudou");

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, EnumSet.of(QueryMode.SIMPLE, QueryMode.AUTOCOMPLETE),
						EnumSet.of(SortConfig.Criteria.SCORE));

		Assert.assertTrue(exceptions.isEmpty());
	}

	@Test
	public void shouldNotConvertQueryInAllModesAllInvalidCritsSolrPublicationCore() {

		SolrPublicationCore core = new SolrPublicationCore("roudoudou");

		Set<SortConfig.Criteria> badCrits = new HashSet<>(Arrays.asList(SortConfig.Criteria.values()));
		badCrits.remove(SortConfig.Criteria.SCORE);

		List<QueryConfiguration.MissingSortConfigException> exceptions =
				convertToSolrQuery(core, EnumSet.of(QueryMode.SIMPLE, QueryMode.AUTOCOMPLETE), badCrits);

		Assert.assertEquals(14, exceptions.size());
	}

	private <F extends SolrField> List<QueryConfiguration.MissingSortConfigException> convertToSolrQuery(SolrCore<F> core, Set<QueryMode> modes, Set<SortConfig.Criteria> badCrits) {

		List<QueryConfiguration.MissingSortConfigException> exceptions = new ArrayList<>();

		for (QueryMode mode : modes) {

			for (SortConfig.Criteria crit : badCrits) {
				Query<F> query = new Query<>(core).sort(crit);
				query.setQueryMode(mode);
				try {
					query.convertToSolrQuery();
				} catch (QueryConfiguration.MissingSortConfigException e) {
					exceptions.add(e);
				}
			}
		}

		return exceptions;
	}
}