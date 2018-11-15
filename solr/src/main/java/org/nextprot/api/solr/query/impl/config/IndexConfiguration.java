package org.nextprot.api.solr.query.impl.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.commons.utils.Pair;
import org.nextprot.api.solr.core.SolrField;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class IndexConfiguration implements QueryConfiguration {

	private static final Log LOGGER = LogFactory.getLog(IndexConfiguration.class);

	private static final String BOOST_SEPARATOR = "^";
	private static final String PLUS = "+";
	public static final String SIMPLE = "simple";

	final static String WHITESPACE = " ";
	
	private String name;
	private final Map<IndexParameter, FieldConfigSet> fieldConfigSets;
	private final Map<String, SortConfig> sortConfigs;
	protected final Map<String, String> otherParameters;

	private String defaultSortName;

	public IndexConfiguration(String name) {
		this.name = name;
		this.fieldConfigSets = new HashMap<>();
		this.sortConfigs = new HashMap<>();
		this.otherParameters = new HashMap<>();
	}
	
	public IndexConfiguration(String name, IndexConfiguration originalConfiguration) {
		this(name);
		
		this.fieldConfigSets.putAll(originalConfiguration.getFieldConfigSets());
		this.sortConfigs.putAll(originalConfiguration.getSortConfigs());
		this.otherParameters.putAll(originalConfiguration.getOtherParameters());
		this.defaultSortName = originalConfiguration.getDefaultSortConfiguration().getName();
	}

	public static IndexConfiguration SIMPLE() {
		return new IndexConfiguration(SIMPLE);
	}

	public void addConfigSet(FieldConfigSet configSet) {
		this.fieldConfigSets.put(configSet.getParameter(), configSet);
	}
	
	public void addSortConfig(SortConfig... sortConfigs) {
		for(SortConfig config : sortConfigs)
			this.sortConfigs.put(config.getName(), config);
	}
	
	public SortConfig getSortConfig(String name) {
		return sortConfigs.getOrDefault(name, null);
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

        String[] tokens = query.getQueryString(true).split(WHITESPACE);

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
		FieldConfigSet configSet;
		
		if(this.fieldConfigSets.containsKey(parameter)) {
			configSet = this.fieldConfigSets.get(parameter);
			
			for(SolrField field : configSet.getIndexFields()) {
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

		String sortName = query.getSort();
		SortConfig sortConfig;

		if (sortName != null) {
			sortConfig = getSortConfig(sortName);

			if (sortConfig == null)
				throw new MissingSortConfigException(sortName, query);
		} else
			sortConfig = getDefaultSortConfiguration();

		if (query.getOrder() != null) {
			for (Pair<SolrField, SolrQuery.ORDER> s : sortConfig.getSorting())
				solrQuery.addSort(s.getFirst().getName(), query.getOrder());

		} else {
			for (Pair<SolrField, SolrQuery.ORDER> s : sortConfig.getSorting())
				solrQuery.addSort(s.getFirst().getName(), s.getSecond());
		}

		if (sortConfig.getBoost() != -1) {
			solrQuery.set("boost", "sum(1.0,product(div(log(informational_score),6.0),div(" + sortConfig.getBoost() + ",100.0)))");
		}

		return solrQuery;
	}

	@Override
	public SolrQuery convertIdQuery(Query query) {

		LOGGER.debug("Query index name:" + query.getIndexName());
		LOGGER.debug("Query config name: "+ query.getConfigName());
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
	
	public Map<IndexParameter, FieldConfigSet> getFieldConfigSets() {
		return fieldConfigSets;
	}

	public Map<String, SortConfig> getSortConfigs() {
		return sortConfigs;
	}

	public String getName() {
		return name;
	}

	public SortConfig getDefaultSortConfiguration() {
		if(this.defaultSortName != null && this.sortConfigs.containsKey(this.defaultSortName))
			return this.sortConfigs.get(this.defaultSortName); 
		else throw new SearchConfigException("No sorting set as default");
	}
	
	public void setDefaultSortName(String defaultSortName) {
		this.defaultSortName = defaultSortName;
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
