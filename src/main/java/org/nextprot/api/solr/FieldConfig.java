package org.nextprot.api.solr;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration of a field
 * 
 * @author mpereira
 *
 */
public class FieldConfig {
	private IndexField field;
	private Map<IndexParameter, Integer> params = new HashMap<IndexParameter, Integer>();

	private FieldConfig(IndexField field) {
		this.field = field;
	}
	
	public static FieldConfig create(IndexField field) {
		return new FieldConfig(field);
	}
	
	public FieldConfig add(IndexParameter param) {
		this.params.put(param, 0);
		return this;
	}
	
	public FieldConfig add(IndexParameter param, int boost) {
		this.params.put(param, boost);
		return this;
	}
	
	public IndexField getField() {
		return field;
	}

	public boolean containsParam(IndexParameter param) {
		return this.params.containsKey(param);
	}
	
	public int getBoost(IndexParameter param) {
		return this.params.get(param);
	}
	
	public boolean equals(FieldConfig config) {
		return this.field.equals(config.getField());
	}
}