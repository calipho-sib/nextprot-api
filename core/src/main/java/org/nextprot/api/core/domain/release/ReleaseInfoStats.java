package org.nextprot.api.core.domain.release;

import java.io.Serializable;
import java.util.List;

public class ReleaseInfoStats implements Serializable{

	private static final long serialVersionUID = 1L;

    private String databaseRelease;

    private List<String> databaseReleaseList;

    private List<ReleaseContentsStatQueries> tagQueries;

    private List<ReleaseStatsTag> tagStatistics;

    public List<ReleaseStatsTag> getTagStatistics() {
		return tagStatistics;
	}

	public void setTagStatistics(List<ReleaseStatsTag> tagStatistics) {
		this.tagStatistics = tagStatistics;
	}

    public String getDatabaseRelease() {
        return databaseRelease;
    }

    public void setDatabaseRelease(String databaseRelease) {
        this.databaseRelease = databaseRelease;
    }

    public List<String> getDatabaseReleaseList() {
        return databaseReleaseList;
    }

    public void setDatabaseReleaseList(List<String> databaseReleaseList) {
        this.databaseReleaseList = databaseReleaseList;
    }

    public List<ReleaseContentsStatQueries> getTagQueries() {
        return tagQueries;
    }

    public void setTagQueries(List<ReleaseContentsStatQueries> tagQueries) {
        this.tagQueries = tagQueries;
    }
}
