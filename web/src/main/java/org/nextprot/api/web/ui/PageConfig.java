package org.nextprot.api.web.ui;

import java.util.List;

public class PageConfig {
	
	List<AnnotationConfig> annotations;
	List<FeatureConfig> features;
	List<String> xrefs;
	
	public PageConfig addFeature(FeatureConfig featureConfig) {
		features.add(featureConfig);
		return this;
	}
	public PageConfig addXref(String xref) {
		xrefs.add(xref);
		return this;
	}
	public List<FeatureConfig> getFeatures() {
		return this.features;
	}
	
	public void setXrefs(List<String> xrefs) {
		this.xrefs=xrefs;
	}
	
	public List<String> getXrefs() {
		return this.xrefs;
	}
	
}
