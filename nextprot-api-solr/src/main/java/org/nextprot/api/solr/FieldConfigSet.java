package org.nextprot.api.solr;

import java.util.HashMap;
import java.util.Map;

public class FieldConfigSet {
		
	private IndexParameter parameter;
	private Map<IndexField, Integer> configs;
	
	private FieldConfigSet(IndexParameter parameter) {
		this.parameter = parameter;
		this.configs = new HashMap<IndexField, Integer>();
	}
	
	public static FieldConfigSet create(IndexParameter parameter) {
		return new FieldConfigSet(parameter);
	}
	
	public FieldConfigSet add(IndexField field) {
		this.configs.put(field, 0);
		return this;
	}
	
	public FieldConfigSet add(IndexField field, int boost) {
		this.configs.put(field, boost);
		return this;
	}

	public IndexParameter getParameter() {
		return parameter;
	}

	public Map<IndexField, Integer> getConfigs() {
		return configs;
	}
	
}
