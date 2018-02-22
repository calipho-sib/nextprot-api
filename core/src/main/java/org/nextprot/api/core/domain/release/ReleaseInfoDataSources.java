package org.nextprot.api.core.domain.release;

import java.io.Serializable;
import java.util.List;

public class ReleaseInfoDataSources implements Serializable{

	private static final long serialVersionUID = 1L;

	private List<ReleaseContentsDataSource> datasources;

	public List<ReleaseContentsDataSource> getDatasources() {
		return datasources;
	}

	public void setDatasources(List<ReleaseContentsDataSource> datasources) {
		this.datasources = datasources;
	}
}
