package org.nextprot.api.web;

import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.utils.NXVelocityUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"StringUtils", "NXUtils"})
public class NXVelocityContext extends VelocityContext {

	public NXVelocityContext() {
		super();
		this.put("StringUtils", StringUtils.class);
		this.put("NXUtils", NXVelocityUtils.class);
	}

	public NXVelocityContext(Entry entry) {
		this();
		this.put("entry", entry);
	}

	public NXVelocityContext(Map<String, Object> map) {
		this();
		if (map != null) {
			for (String key : map.keySet()) {
				this.put(key, map.get(key));
			}
		}
	}

}
