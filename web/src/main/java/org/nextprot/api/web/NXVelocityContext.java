package org.nextprot.api.web;

import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.utils.NXVelocityUtils;

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

}
