package org.nextprot.api.web.seo.domain;

public class SeoTags {
	
	String title;
	String metaDescription;
	String h1;
	
	public SeoTags() {}
	
	public SeoTags(String title, String metaDescription, String h1) {
		this.title=title;
		this.h1=h1;
		this.metaDescription=metaDescription;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMetaDescription() {
		return metaDescription;
	}
	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}
	public String getH1() {
		return h1;
	}
	public void setH1(String h1) {
		this.h1 = h1;
	}

	
	
}
