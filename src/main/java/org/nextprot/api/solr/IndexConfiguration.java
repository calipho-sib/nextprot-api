package org.nextprot.api.solr;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nextprot.core.exception.SearchConfigException;

public class IndexConfiguration implements QueryBuilder {
	protected final String BOOST_SPEARATOR = "^";
	protected final String PLUS = "+";
	protected final String WHITESPACE = " ";
	
	private String name;
	protected Map<IndexParameter, FieldConfigSet> fieldConfigSets;
	protected Map<String, SortConfig> sortConfigs;
	protected Map<String, String> otherParameters;
	
	protected String defaultSortName;
	
	public IndexConfiguration(String name) {
		this.name = name;
		this.fieldConfigSets = new HashMap<IndexParameter, FieldConfigSet>();
		this.sortConfigs = new HashMap<String, SortConfig>();
		this.otherParameters = new HashMap<String, String>();
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
	
	public FieldConfigSet getConfigSet(IndexParameter parameter) {
		return this.fieldConfigSets.get(parameter);
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
		String[] tokens = query.getQueryString().split(WHITESPACE);
		
		for(int i=0; i<tokens.length; i++) {
			queryBuilder.append(PLUS+tokens[i]);
			
			if(i != tokens.length - 1)
				queryBuilder.append(WHITESPACE);
		}
		
		this.otherParameters.put("spellcheck.q", query.getQueryString());
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
			
			for(Entry<IndexField, Integer> e : configSet.getConfigs().entrySet()) {
				builder.append(e.getKey().getName());
				if(e.getValue() > 0) builder.append(BOOST_SPEARATOR+e.getValue());
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

	public void setFieldConfigSets(
			Map<IndexParameter, FieldConfigSet> fieldConfigSets) {
		this.fieldConfigSets = fieldConfigSets;
	}

	public Map<String, SortConfig> getSortConfigs() {
		return sortConfigs;
	}

	public void setSortConfigs(Map<String, SortConfig> sortConfigs) {
		this.sortConfigs = sortConfigs;
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
