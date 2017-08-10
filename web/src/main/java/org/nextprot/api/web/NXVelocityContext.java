package org.nextprot.api.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.release.ReleaseInfo;
import org.nextprot.api.core.utils.NXVelocityUtils;

import java.util.Map;

@JsonIgnoreProperties({"StringUtils", "NXUtils"})
public class NXVelocityContext extends VelocityContext {

	final static public String ENTRIES_COUNT = "entriesCount";
	final static public String RELEASE_NUMBER = "release";

	public NXVelocityContext() {
		super();
		put("StringUtils", StringUtils.class);
		put("NXUtils", NXVelocityUtils.class);
	}

	public NXVelocityContext(Entry entry) {
		this();
		this.put("entry", entry);
	}

	public NXVelocityContext(int entryNum, ReleaseInfo releaseInfo) {
		this();

		this.put(ENTRIES_COUNT, entryNum);
		this.put(RELEASE_NUMBER, releaseInfo);
	}

	public void add(String key, Object value) {

		put(key, value);
	}
}
