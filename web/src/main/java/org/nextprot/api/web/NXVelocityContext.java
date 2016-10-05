package org.nextprot.api.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.utils.NXVelocityUtils;

import java.util.Map;

@JsonIgnoreProperties({"StringUtils", "NXUtils"})
public class NXVelocityContext extends VelocityContext {

	public NXVelocityContext() {
		super();
		put("StringUtils", StringUtils.class);
		put("NXUtils", NXVelocityUtils.class);
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
