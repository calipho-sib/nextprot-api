package org.nextprot.api.core.domain.release;

public class ReleaseInfoDataSource {
	
	private String source, description, url, release, lastImportDate;

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

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getLastImportDate() {
		return lastImportDate;
	}

	public void setLastImportDate(String lastImportDate) {
		this.lastImportDate = lastImportDate;
	}
	
}
