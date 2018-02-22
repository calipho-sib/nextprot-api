package org.nextprot.api.core.domain.release;

import java.io.Serializable;
import java.util.List;

public class ReleaseInfoStats implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<ReleaseStatsTag> tagStatistics;
	
	public List<ReleaseStatsTag> getTagStatistics() {
		return tagStatistics;
	}

	public void setTagStatistics(List<ReleaseStatsTag> tagStatistics) {
		this.tagStatistics = tagStatistics;
	}
}
