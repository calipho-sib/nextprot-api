package org.nextprot.api.core.domain.release;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReleaseInfoStats implements Serializable{

	private static final long serialVersionUID = 1L;

    private String databaseRelease;

    private List<String> databaseReleaseList;

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

    public void addDatabaseReleaseToList(String databaseRelease) {
	    if (this.databaseReleaseList == null || this.databaseReleaseList.isEmpty()) {
            this.databaseReleaseList = new ArrayList<>();
        }
        this.databaseReleaseList.add(databaseRelease);
    }
}
