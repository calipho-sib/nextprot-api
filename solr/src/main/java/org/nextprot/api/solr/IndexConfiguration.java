package org.nextprot.api.solr;

import org.nextprot.api.commons.exception.SearchConfigException;

import java.util.HashMap;
import java.util.Map;

public class IndexConfiguration implements QueryBuilder {
	protected final String BOOST_SEPARATOR = "^";
	protected final String PLUS = "+";
	protected final String WHITESPACE = " ";
	
	private String name;
	protected Map<IndexParameter, FieldConfigSet> fieldConfigSets;
	protected Map<String, SortConfig> sortConfigs;
	protected Map<String, String> otherParameters;
	
	protected String defaultSortName;
	
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

	public void addConfigSet(FieldConfigSet configSet) {
		this.fieldConfigSets.put(configSet.getParameter(), configSet);
	}
	
	public void addSortConfig(SortConfig... sortConfigs) {
		for(SortConfig config : sortConfigs)
			this.sortConfigs.put(config.getName(), config);
	}
	
	public SortConfig getSortConfig(String name) {
		return this.sortConfigs.containsKey(name) ? this.sortConfigs.get(name) : null;
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
	public String buildQuery(Query query) {
		StringBuilder queryBuilder = new StringBuilder();

        String[] tokens = query.getQueryString(true).split(WHITESPACE);

        for (int i = 0; i < tokens.length; i++) {
            queryBuilder.append(PLUS + tokens[i]);

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
			
			for(IndexField field : configSet.getIndexFields()) {
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
