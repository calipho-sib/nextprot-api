package org.nextprot.api.web.seo.domain;

public class SeoTagsAndUrl extends SeoTags {
	
	String url;
	
	public SeoTagsAndUrl() { super(); }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
