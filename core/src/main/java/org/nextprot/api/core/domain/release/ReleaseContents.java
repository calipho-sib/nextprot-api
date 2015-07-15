package org.nextprot.api.core.domain.release;

import java.io.Serializable;
import java.util.List;

public class ReleaseContents implements Serializable{

	private static final long serialVersionUID = 2368364380055302455L;

	private String databaseRelease;
	private String apiRelease;
	private List<ReleaseContentsDataSource> datasources;

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

	public List<ReleaseContentsDataSource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<ReleaseContentsDataSource> datasources) {
		this.datasources = datasources;
	}

}
