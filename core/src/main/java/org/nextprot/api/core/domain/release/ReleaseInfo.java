package org.nextprot.api.core.domain.release;

import java.util.List;

public class ReleaseInfo {

	private String databaseRelease;
	private String apiRelease;
	private List<ReleaseInfoDataSource> datasources;

	public String getDatabaseRelease() {
		return databaseRelease;
	}

	public void setDatabaseRelease(String databaseRelease) {
		this.databaseRelease = databaseRelease;
	}

	public String getApiRelease() {
		return apiRelease;
	}

	public void setApiRelease(String apiRelease) {
		this.apiRelease = apiRelease;
	}

	public List<ReleaseInfoDataSource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<ReleaseInfoDataSource> datasources) {
		this.datasources = datasources;
	}

}
