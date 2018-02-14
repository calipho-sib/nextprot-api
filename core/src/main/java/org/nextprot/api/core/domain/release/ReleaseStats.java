package org.nextprot.api.core.domain.release;

import java.io.Serializable;
import java.util.List;

public class ReleaseStats implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<ReleaseContentsDataSource> datasources;
	private List<ReleaseStatsTag> tagStatistics;
	
	public List<ReleaseStatsTag> getTagStatistics() {
		return tagStatistics;
	}

	public void setTagStatistics(List<ReleaseStatsTag> tagStatistics) {
		this.tagStatistics = tagStatistics;
	}

	public List<ReleaseContentsDataSource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<ReleaseContentsDataSource> datasources) {
		this.datasources = datasources;
	}

}
