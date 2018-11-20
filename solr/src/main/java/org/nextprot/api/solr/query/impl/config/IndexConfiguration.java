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
import java.util.List;
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
	
	public IndexConfiguration(QueryMode mode, IndexConfiguration originalConfiguration) {
		this(mode);
		
		this.fieldConfigSets.putAll(originalConfiguration.getFieldConfigSets());
		this.sortConfigs.putAll(originalConfiguration.getSortConfigs());
		this.otherParameters.putAll(originalConfiguration.getOtherParameters());
		this.defaultSortCriteria = originalConfiguration.getDefaultSortConfiguration().getCriteria();
	}

	public void addConfigSet(FieldConfigSet<F> configSet) {
		this.fieldConfigSets.put(configSet.getParameter(), configSet);
	}

	public void addSortConfig(SortConfig<F> sortConfig) {
		this.sortConfigs.put(sortConfig.getCriteria(), sortConfig);
	}

	public void addSortConfigs(List<SortConfig<F>> sortConfigs) {
		for(SortConfig<F> config : sortConfigs)
			this.sortConfigs.put(config.getCriteria(), config);
	}
	
	public SortConfig<F> getSortConfig(SortConfig.Criteria criteria) {
		return sortConfigs.getOrDefault(criteria, null);
	}
	
	public IndexConfiguration addOtherParameter(String parameterName, String parameterValue) {
		this.otherParameters.put(parameterName, parameterValue);
		return this;
	}
	
	/**
	 * It splits the query coming for the controller in tokens 
	 * and builds the query to Solr accordingly
	 * 
	 * @param query
	 * @return
	 */
	@Override
	public String formatQuery(Query query) {
		StringBuilder queryBuilder = new StringBuilder();

        String[] tokens = query.getQueryStringEscapeColon().split(WHITESPACE);

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
				    builder.append(BOOST_SEPARATOR +boost);
                }
				builder.append(WHITESPACE);
			}
		}
		return builder.toString().trim();
	}

	@Override
	public SolrQuery convertQuery(Query query) throws MissingSortConfigException {

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

		if (criteria != null) {
			sortConfig = getSortConfig(criteria);

			if (sortConfig == null)
				throw new MissingSortConfigException(criteria, query);
		} else
			sortConfig = getDefaultSortConfiguration();

		if (query.getOrder() != null) {
			for (SortConfig.SortBy<F> s : sortConfig.getSorting())
				solrQuery.addSort(s.getField().getName(), query.getOrder());

		} else {
			for (SortConfig.SortBy<F> s : sortConfig.getSorting())
				solrQuery.addSort(s.getField().getName(), s.getOrder());
		}

		if (sortConfig.getBoost() != -1) {
			solrQuery.set("boost", "sum(1.0,product(div(log(informational_score),6.0),div(" + sortConfig.getBoost() + ",100.0)))");
		}

		return solrQuery;
	}

	@Override
	public SolrQuery convertIdQuery(Query query) {

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
		if(defaultSortCriteria != null && sortConfigs.containsKey(this.defaultSortCriteria))
			return sortConfigs.get(defaultSortCriteria);
		else throw new SearchConfigException("No sorting set as default");
	}
	
	public void setDefaultSortCriteria(SortConfig.Criteria defaultSortCriteria) {
		this.defaultSortCriteria = defaultSortCriteria;
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
