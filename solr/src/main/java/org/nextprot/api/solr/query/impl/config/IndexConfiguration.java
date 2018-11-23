package org.nextprot.api.solr.query.impl.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.nextprot.api.commons.exception.NPreconditions;
import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.core.impl.settings.FieldConfigSet;
import org.nextprot.api.solr.core.impl.settings.IndexParameter;
import org.nextprot.api.solr.core.impl.settings.SortConfig;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryConfiguration;
import org.nextprot.api.solr.query.QueryMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class IndexConfiguration<F extends SolrField> implements QueryConfiguration<F> {

	private static final Log LOGGER = LogFactory.getLog(IndexConfiguration.class);

	private static final String BOOST_SEPARATOR = "^";
	private static final String PLUS = "+";

	final static String WHITESPACE = " ";
	
	private QueryMode mode;
	private final Map<IndexParameter, FieldConfigSet<F>> fieldConfigSets;
	private final Map<SortConfig.Criteria, SortConfig<F>> sortConfigs;
	protected final Map<String, String> otherParameters;

	private SortConfig.Criteria defaultSortCriteria;

	public IndexConfiguration(QueryMode mode) {
		NPreconditions.checkNotNull(mode, "Solr query configuration mode is undefined");

		this.mode = mode;
		this.fieldConfigSets = new HashMap<>();
		this.sortConfigs = new HashMap<>();
		this.otherParameters = new HashMap<>();
	}
	
	public IndexConfiguration(QueryMode mode, IndexConfiguration<F> originalConfiguration) {
		this(mode);
		
		this.fieldConfigSets.putAll(originalConfiguration.getFieldConfigSets());
		this.sortConfigs.putAll(originalConfiguration.getSortConfigs());
		this.otherParameters.putAll(originalConfiguration.getOtherParameters());
		this.defaultSortCriteria = originalConfiguration.getDefaultSortCriteria();
	}

	public void addConfigSet(FieldConfigSet<F> configSet) {
		this.fieldConfigSets.put(configSet.getParameter(), configSet);
	}

	public void addSortConfig(SortConfig.Criteria criteria, SortConfig<F> sortConfig) {
		if (sortConfigs.containsKey(criteria)) {
			throw new IllegalStateException("already contains sorting criteria "+criteria);
		}
		this.sortConfigs.put(criteria, sortConfig);
	}

	public void addSortConfigs(Map<SortConfig.Criteria, SortConfig<F>> map) {

	    for (Map.Entry<SortConfig.Criteria, SortConfig<F>> entry : map.entrySet()) {
			addSortConfig(entry.getKey(), entry.getValue());
		}
	}
	
	public SortConfig<F> getSortConfig(SortConfig.Criteria criteria) {
		return sortConfigs.getOrDefault(criteria, null);
	}
	
	public IndexConfiguration addOtherParameter(String parameterName, String parameterValue) {
		this.otherParameters.put(parameterName, parameterValue);
		return this;
	}

	@Override
	public String formatQuery(Query<F> query) {

		return (query != null) ? prefixAllTermsWithPlusOperator(query.getQueryStringEscapeColon()) : "";
	}

	/**
	 * Add a '+' symbol (also known as the "required" operator) before all terms in the query string.
	 *
	 * https://lucene.apache.org/solr/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-TheBooleanOperator_
	 *
	 * @param queryString the query to modify
	 * @return the query with all terms prefixed with the "required" operator
	 */
	private String prefixAllTermsWithPlusOperator(String queryString) {

		StringBuilder queryBuilder = new StringBuilder();

		String[] tokens = queryString.split(WHITESPACE);

		for (int i = 0; i < tokens.length; i++) {
			queryBuilder.append(PLUS).append(tokens[i]);

			if (i != tokens.length - 1)
				queryBuilder.append(WHITESPACE);
		}

		//this.otherParameters.put("spellcheck.q", query.getQueryString());
		return queryBuilder.toString();
	}

	/**
	 * Builds a query for a specified parameter ex. FL, QF, etc
	 * If a variable has a defined boost for the asked parameter the boost
	 * will be added to the query
	 * @param parameter
	 * @return
	 */
	public String getParameterQuery(IndexParameter parameter) {
		StringBuilder builder = new StringBuilder();
		FieldConfigSet<F> configSet;
		
		if(this.fieldConfigSets.containsKey(parameter)) {
			configSet = this.fieldConfigSets.get(parameter);
			
			for(F field : configSet.getIndexFields()) {
                int boost = configSet.getBoostFactor(field);

				builder.append(field.getName());
				if(boost > 0) {
				    builder.append(BOOST_SEPARATOR).append(boost);
                }
				builder.append(WHITESPACE);
			}
		}
		return builder.toString().trim();
	}

	@Override
	public SolrQuery convertQuery(Query<F> query) throws MissingSortConfigException {

		SolrQuery solrQuery = new SolrQuery();

		String queryString = formatQuery(query);

		String filter = query.getFilter();
		if (filter != null)
			queryString += " AND filters:" + filter;

		solrQuery.setQuery(queryString);
		solrQuery.setStart(query.getStart());
		solrQuery.setRows(query.getRows());
		solrQuery.setFields(getParameterQuery(IndexParameter.FL));
		solrQuery.set(IndexParameter.FL.name().toLowerCase(), getParameterQuery(IndexParameter.FL));
		solrQuery.set(IndexParameter.QF.name().toLowerCase(), getParameterQuery(IndexParameter.QF));
		solrQuery.set(IndexParameter.PF.name().toLowerCase(), getParameterQuery(IndexParameter.PF));
		solrQuery.set(IndexParameter.FN.name().toLowerCase(), getParameterQuery(IndexParameter.FN));
		solrQuery.set(IndexParameter.HI.name().toLowerCase(), getParameterQuery(IndexParameter.HI));

		Map<String, String> otherParameters = getOtherParameters();

		if (otherParameters != null)
			for (Map.Entry<String, String> e : otherParameters.entrySet())
				solrQuery.set(e.getKey(), e.getValue());

        SortConfig.Criteria criteria = query.getSort();
		SortConfig<F> sortConfig;

		// sorting based solely on sortConfig field
		if (query.getSortConfig() != null) {
			sortConfig = query.getSortConfig();
			for (SortConfig.SortBy<F> sb : sortConfig.getSorting()) {
				solrQuery.addSort(sb.getField().getName(), sb.getOrder());
			}
		}
		// sorting based on criteria and order
		else {
			if (criteria != null) {
				sortConfig = getSortConfig(criteria);

				if (sortConfig == null)
					throw new MissingSortConfigException(criteria, query);
			} else {
				sortConfig = getDefaultSortConfiguration();
			}

			if (query.getOrder() != null) {
				for (SortConfig.SortBy<F> sb : sortConfig.getSorting()) {
					solrQuery.addSort(sb.getField().getName(), query.getOrder());
				}
			} else {
				for (SortConfig.SortBy<F> sb : sortConfig.getSorting()) {
					solrQuery.addSort(sb.getField().getName(), sb.getOrder());
				}
			}
		}

		if (sortConfig.getBoost() != -1) {
			solrQuery.set("boost", "sum(1.0,product(div(log(informational_score),6.0),div(" + sortConfig.getBoost() + ",100.0)))");
		}

		return solrQuery;
	}

	@Override
	public SolrQuery convertIdQuery(Query<F> query) {

		LOGGER.debug("Query index name:" + query.getIndexName());
		LOGGER.debug("Query config name: "+ query.getQueryMode().getName());
		String solrReadyQueryString = formatQuery(query);
		String filter = query.getFilter();
		if (filter != null)
			solrReadyQueryString += " AND filters:" + filter;

		LOGGER.debug("Solr-ready query       : " + solrReadyQueryString);

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(solrReadyQueryString);
		solrQuery.setRows(0);
		solrQuery.set("facet", true);
		solrQuery.set("facet.field", "id");
		solrQuery.set("facet.method", "enum");
		solrQuery.set("facet.query", solrReadyQueryString);
		solrQuery.set("facet.limit", 30000);
		logSolrQuery("convertIdQuery", solrQuery);

		return solrQuery;
	}

	// 
	//	Getters & Setters
	//
	
	public Map<IndexParameter, FieldConfigSet<F>> getFieldConfigSets() {
		return fieldConfigSets;
	}

	public Map<SortConfig.Criteria, SortConfig<F>> getSortConfigs() {
		return sortConfigs;
	}

	public QueryMode getMode() {
		return mode;
	}

	public SortConfig<F> getDefaultSortConfiguration() {
		if(defaultSortCriteria != null && sortConfigs.containsKey(defaultSortCriteria))
			return sortConfigs.get(defaultSortCriteria);
		else throw new SearchConfigException("No sorting set as default");
	}
	
	public void setDefaultSortCriteria(SortConfig.Criteria defaultSortCriteria) {
		this.defaultSortCriteria = defaultSortCriteria;
	}

    public SortConfig.Criteria getDefaultSortCriteria() {
        return defaultSortCriteria;
    }

	public Map<String, String> getOtherParameters() {
		return this.otherParameters;
	}

	private void logSolrQuery(String context, SolrQuery sq) {
		Set<String> params = new TreeSet<>();
		for (String p : sq.getParameterNames()) params.add(p + " : " + sq.get(p));
		LOGGER.debug("SolrQuery ============================================================== in " + context);
		for (String p : params) {
			LOGGER.debug("SolrQuery " + p);
		}
	}
}
