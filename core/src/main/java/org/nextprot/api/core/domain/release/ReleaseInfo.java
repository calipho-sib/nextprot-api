package org.nextprot.api.core.domain.release;

import java.util.List;

public class ReleaseInfo {

	private String databaseRelease;
	private String applicationRelease;
	private List<ReleaseInfoDataSource> datasources;

	public String getDatabaseRelease() {
		return databaseRelease;
	}

	public void setDatabaseRelease(String databaseRelease) {
		this.databaseRelease = databaseRelease;
	}

	public String getApplicationRelease() {
		return applicationRelease;
	}

	public void setApplicationRelease(String applicationRelease) {
		this.applicationRelease = applicationRelease;
	}

	public List<ReleaseInfoDataSource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<ReleaseInfoDataSource> datasources) {
		this.datasources = datasources;
	}
	
}

