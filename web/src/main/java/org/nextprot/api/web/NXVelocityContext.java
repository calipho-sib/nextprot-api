package org.nextprot.api.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.velocity.VelocityContext;
import org.nextprot.api.commons.utils.StringUtils;
import org.nextprot.api.core.domain.Entry;
import org.nextprot.api.core.domain.release.ReleaseInfoDataSources;
import org.nextprot.api.core.domain.release.ReleaseInfoVersions;
import org.nextprot.api.core.utils.NXVelocityUtils;

@JsonIgnoreProperties({"StringUtils", "NXUtils"})
public class NXVelocityContext extends VelocityContext {

	final static public String ENTRIES_COUNT = "entriesCount";
	final static public String RELEASE_NUMBER = "versions";
	final static public String RELEASE_DATA_SOURCES = "dataSources";
	final static public String RELEASE_STATS = "releaseStats";

	public NXVelocityContext() {
		super();
		put("StringUtils", StringUtils.class);
		put("NXUtils", NXVelocityUtils.class);
	}

	public NXVelocityContext(Entry entry) {
		this();
		this.put("entry", entry);
	}

	public NXVelocityContext(int entryNum, ReleaseInfoVersions releaseInfoVersions) {
		this();

		this.put(ENTRIES_COUNT, entryNum);
		this.put(RELEASE_NUMBER, releaseInfoVersions);
	}

	public NXVelocityContext(int entryNum, ReleaseInfoVersions releaseInfoVersions, ReleaseInfoDataSources releaseInfoDataSources) {
		this();

		this.put(ENTRIES_COUNT, entryNum);
		this.put(RELEASE_NUMBER, releaseInfoVersions);
		this.put(RELEASE_DATA_SOURCES, releaseInfoDataSources);
	}

	public void add(String key, Object value) {

		put(key, value);
	}
}
