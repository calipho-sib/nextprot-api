package org.nextprot.api.core.domain.release;

import java.io.Serializable;

public class ReleaseContentsDataSource implements Serializable{
	
	private static final long serialVersionUID = -6596635331160549824L;
	
	private String source, description, url, releaseVersion, lastImportDate;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getReleaseVersion() {
		return releaseVersion;
	}

	public void setReleaseVersion(String releaseVersion) {
		this.releaseVersion = releaseVersion;
	}

	public String getLastImportDate() {
		return lastImportDate;
	}

	public void setLastImportDate(String lastImportDate) {
		this.lastImportDate = lastImportDate;
	}
	
}
