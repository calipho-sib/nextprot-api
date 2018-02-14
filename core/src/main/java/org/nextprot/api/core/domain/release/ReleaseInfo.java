package org.nextprot.api.core.domain.release;

import java.io.Serializable;

public class ReleaseInfo implements Serializable {

	private static final long serialVersionUID = 2L;

	private String databaseRelease;
	private String apiRelease;

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
}
