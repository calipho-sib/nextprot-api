package org.nextprot.api.solr.query.config;

import org.nextprot.api.commons.exception.SearchConfigException;
import org.nextprot.api.solr.query.Query;
import org.nextprot.api.solr.query.QueryBuilder;
import org.nextprot.api.solr.core.SolrField;

import java.util.HashMap;
import java.util.Map;

public class IndexConfiguration implements QueryBuilder {

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
	public String buildQuery(Query query) {
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

}
