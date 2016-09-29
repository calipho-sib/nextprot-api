package org.nextprot.api.web.ui;

import org.nextprot.api.commons.constants.AnnotationCategory;

public class FeatureConfig {

	String APIRef;
	AnnotationCategory annotationCategory;
	Metadata metadata;

	public FeatureConfig(String apiref, AnnotationCategory cat, Metadata metadata) {
		this.APIRef=apiref;
		this.annotationCategory=cat;
		this.metadata=metadata;
	}		
	
	public AnnotationCategory getAnnotationCategory() {
		return annotationCategory;
	}

	public String getAPIRef() {
		return APIRef;
	}
	public Metadata getMetadata() {
		return metadata;
	}

	public class Metadata {
		String name;
		String className;
		String color;
		String type;
		String filter;
		public Metadata(String name, String className,String color,String type, String filter) {
			this.name=name;
			this.className=className;
			this.color=color;
			this.type=type;
			this.filter=filter;
		}
		public String getName() {
			return name;
		}
		public String getClassName() {
			return className;
		}
		public String getColor() {
			return color;
		}
		public String getType() {
			return type;
		}
		public String getFilter() {
			return filter;
		}
	}

}


